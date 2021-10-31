package tech.salroid.filmy.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import tech.salroid.filmy.R
import com.miguelcatalan.materialsearchview.MaterialSearchView
import android.widget.TextView
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import tr.xip.errorview.ErrorView
import tech.salroid.filmy.fragment.Trending
import tech.salroid.filmy.fragment.SearchFragment
import tech.salroid.filmy.customs.CustomToast
import android.os.Bundle
import android.preference.PreferenceManager
import butterknife.ButterKnife
import androidx.core.content.ContextCompat
import com.miguelcatalan.materialsearchview.MaterialSearchView.SearchViewListener
import tech.salroid.filmy.FilmyIntro
import tech.salroid.filmy.network_stuff.FirstFetch
import com.google.android.material.appbar.AppBarLayout
import tech.salroid.filmy.fragment.InTheaters
import tech.salroid.filmy.fragment.UpComing
import tech.salroid.filmy.custom_adapter.MyPagerAdapter
import android.graphics.Color
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.widget.Toolbar
import tech.salroid.filmy.utility.Network

/*
 * Filmy Application for Android
 * Copyright (c) 2016 Sajal Gupta (http://github.com/salroid).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class MainActivity : AppCompatActivity() {
    @JvmField
    var fetchingFromNetwork = false

    @JvmField
    @BindView(R.id.toolbar)
    var toolbar: Toolbar? = null

    @JvmField
    @BindView(R.id.search_view)
    var materialSearchView: MaterialSearchView? = null

    @JvmField
    @BindView(R.id.logo)
    var logo: TextView? = null

    @JvmField
    @BindView(R.id.toolbarScroller)
    var toolbarScroller: FrameLayout? = null

    @JvmField
    @BindView(R.id.viewpager)
    var viewPager: ViewPager? = null

    @JvmField
    @BindView(R.id.tab_layout)
    var tabLayout: TabLayout? = null

    @JvmField
    @BindView(R.id.main_error_view)
    var mErrorView: ErrorView? = null
    private var trendingFragment: Trending? = null
    private var searchFragment: SearchFragment? = null
    private var cantProceed = false
    private var nightMode = false
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Extract data included in the Intent
            val statusCode = intent.getIntExtra("message", 0)
            CustomToast.show(context, "Failed to get latest movies.", true)
            cantProceed(statusCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) supportActionBar!!.setTitle(" ")
        introLogic()
        if (nightMode) allThemeLogic()

        mErrorView?.apply {
            title = getString(R.string.error_title_damn)
            titleColor = ContextCompat.getColor(context, R.color.dark)
            setSubtitle(getString(R.string.error_details))
            setRetryText(getString(R.string.error_retry))
        }

        mErrorView!!.setRetryListener {
            if (Network.isNetworkConnected(this@MainActivity)) {
                fetchingFromNetwork = true
                setScheduler()
            }
            canProceed()
        }
        mErrorView!!.visibility = View.GONE
        setupViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
        materialSearchView!!.setVoiceSearch(true)
        materialSearchView!!.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //Do some magic
                getSearchedResult(query)
                searchFragment!!.showProgress()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {

                //getSearchedResult(newText);
                return true
            }
        })
        materialSearchView!!.setOnSearchViewListener(object : SearchViewListener {
            override fun onSearchViewShown() {
                //Do some magic
                tabLayout!!.visibility = View.GONE
                disableToolbarScrolling()
                searchFragment = SearchFragment()
                supportFragmentManager.beginTransaction().replace(R.id.search_container, searchFragment!!)
                        .commit()
            }

            override fun onSearchViewClosed() {
                //Do some magic
                supportFragmentManager
                        .beginTransaction()
                        .remove(searchFragment!!)
                        .commit()
                if (!cantProceed) tabLayout!!.visibility = View.VISIBLE
                enableToolbarScrolling()
            }
        })
        if (Network.isNetworkConnected(this)) {
            fetchingFromNetwork = true
            setScheduler()
        }
    }

    private fun allThemeLogic() {
        tabLayout!!.setTabTextColors(Color.parseColor("#bdbdbd"), Color.parseColor("#e0e0e0"))
        tabLayout!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDarkThemePrimary))
        tabLayout!!.setSelectedTabIndicatorColor(Color.parseColor("#bdbdbd"))
        logo!!.setTextColor(Color.parseColor("#E0E0E0"))
        materialSearchView!!.setBackgroundColor(resources.getColor(R.color.colorDarkThemePrimary))
        materialSearchView!!.setBackIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_navigation_arrow_back_inverted))
        materialSearchView!!.setCloseIcon(ContextCompat.getDrawable(this, R.drawable.ic_action_navigation_close_inverted))
        materialSearchView!!.setTextColor(Color.parseColor("#ffffff"))
    }

    private fun introLogic() {
        val t = Thread {
            val getPrefs = PreferenceManager
                    .getDefaultSharedPreferences(baseContext)
            val isFirstStart = getPrefs.getBoolean("firstStart", true)
            if (isFirstStart) {
                val i = Intent(this@MainActivity, FilmyIntro::class.java)
                startActivity(i)
                val e = getPrefs.edit()
                e.putBoolean("firstStart", false)
                e.apply()
            }
        }
        t.start()
    }

    private fun setScheduler() {
        val firstFetch = FirstFetch(this)
        firstFetch.start()
    }

    fun cantProceed(status: Int) {
        Handler().postDelayed({
            if (trendingFragment != null && !trendingFragment!!.isShowingFromDatabase) {
                cantProceed = true
                tabLayout!!.visibility = View.GONE
                viewPager!!.visibility = View.GONE
                //mErrorView.setError(status);
                mErrorView!!.visibility = View.VISIBLE
                //disable toolbar scrolling
                disableToolbarScrolling()
            }
        }, 1000)
    }

    fun canProceed() {
        cantProceed = false
        tabLayout!!.visibility = View.VISIBLE
        viewPager!!.visibility = View.VISIBLE
        mErrorView!!.visibility = View.GONE
        if (trendingFragment != null) {
            trendingFragment!!.retryLoading()
        }
        enableToolbarScrolling()
    }

    private fun disableToolbarScrolling() {
        val params = toolbarScroller!!.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
    }

    private fun enableToolbarScrolling() {
        val params = toolbarScroller!!.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        trendingFragment = Trending()
        val inTheatersFragment = InTheaters()
        val upComingFragment = UpComing()
        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.addFragment(trendingFragment, getString(R.string.trending))
        adapter.addFragment(inTheatersFragment, getString(R.string.theatres))
        adapter.addFragment(upComingFragment, getString(R.string.upcoming))
        viewPager!!.adapter = adapter
    }

    private fun getSearchedResult(query: String) {
        if (searchFragment != null) {
            searchFragment!!.getSearchedResult(query)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val itemSearch = menu.findItem(R.id.action_search)
        val itemAccount = menu.findItem(R.id.ic_collections)
        if (nightMode) {
            itemSearch.setIcon(R.drawable.ic_action_action_search)
            itemAccount.setIcon(R.drawable.ic_action_collections_bookmark2)
        }
        materialSearchView!!.setMenuItem(itemSearch)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_search -> startActivity(Intent(this, SearchFragment::class.java))
            R.id.ic_setting -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.ic_collections -> startActivity(Intent(this, CollectionsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            val matches = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null && matches.size > 0) {
                val searchWrd = matches[0]
                if (!TextUtils.isEmpty(searchWrd)) {
                    materialSearchView!!.setQuery(searchWrd, false)
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (materialSearchView!!.isSearchOpen) {
            materialSearchView!!.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                IntentFilter("fetch-failed"))
    }

    override fun onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onPause()
    }
}