package tech.salroid.filmy.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.miguelcatalan.materialsearchview.MaterialSearchView.SearchViewListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.data.network.NetworkUtil
import tech.salroid.filmy.databinding.ActivityMainBinding
import tech.salroid.filmy.ui.FilmyIntro
import tech.salroid.filmy.ui.activities.fragment.InTheaters
import tech.salroid.filmy.ui.activities.fragment.Trending
import tech.salroid.filmy.ui.activities.fragment.UpComing
import tech.salroid.filmy.ui.adapters.CollectionsPagerAdapter
import tech.salroid.filmy.ui.fragment.SearchFragment
import tech.salroid.filmy.utility.getQueryTextChangeStateFlow

class MainActivity : AppCompatActivity() {

    var fetchingFromNetwork = false
    private var trendingFragment: Trending? = null
    private lateinit var searchFragment: SearchFragment
    private var cantProceed = false
    private var nightMode = false
    private lateinit var binding: ActivityMainBinding

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val statusCode = intent.getIntExtra("message", 0)
            cantProceed(statusCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = " "
        introLogic()
        if (nightMode) allThemeLogic() else lightThemeLogic()

       /* binding.mainErrorView.apply {
            title = getString(R.string.error_title_damn)
            titleColor = ContextCompat.getColor(context, R.color.dark)
            setSubtitle(getString(R.string.error_details))
            setRetryText(getString(R.string.error_retry))
        }*/

        /*binding.mainErrorView.setRetryListener {
            if (NetworkUtil.isNetworkConnected(this@MainActivity)) {
                fetchingFromNetwork = true
                // fetchMoviesFromNetwork()
            }
            canProceed()
        }*/

        //binding.mainErrorView.visibility = View.GONE

        setupSearch()
    }

    private fun setupSearch() {
        binding.searchView.setVoiceSearch(false)
        binding.searchView.setOnSearchViewListener(object : SearchViewListener {
            override fun onSearchViewShown() {
                binding.tabLayout.visibility = View.GONE
                disableToolbarScrolling()
                searchFragment = SearchFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.search_container, searchFragment)
                    .commit()
            }

            override fun onSearchViewClosed() {
                supportFragmentManager
                    .beginTransaction()
                    .remove(searchFragment)
                    .commit()
                enableToolbarScrolling()
            }
        })

        // Instant search using Flow
        lifecycleScope.launch(Dispatchers.IO) {
            binding.searchView.getQueryTextChangeStateFlow()
                .debounce(300)
                .filter { query ->
                    return@filter query.isNotEmpty()
                }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    searchFragment.showProgress()
                    flow {
                        emit(NetworkUtil.searchMovies(query))
                    }.catch {
                        emitAll(flowOf(null))
                    }
                }
                .flowOn(Dispatchers.Main)
                .collect { result ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        result?.results?.let { searchFragment.showSearchResults(it) }
                    }
                }
        }
    }

    private fun allThemeLogic() {
        binding.tabLayout.setTabTextColors(Color.parseColor("#bdbdbd"), Color.parseColor("#e0e0e0"))
        binding.tabLayout.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorDarkThemePrimary
            )
        )
        binding.tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#bdbdbd"))
        binding.logo.setTextColor(Color.parseColor("#E0E0E0"))
        binding.searchView.setBackgroundColor(resources.getColor(R.color.colorDarkThemePrimary))
        binding.searchView.setBackIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_action_navigation_arrow_back_inverted
            )
        )
        binding.searchView.setCloseIcon(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_action_navigation_close_inverted
            )
        )
        binding.searchView.setTextColor(Color.parseColor("#ffffff"))
    }

    private fun lightThemeLogic() {
        binding.logo.setTextColor(ContextCompat.getColor(this, R.color.dark))
    }

    private fun introLogic() {
        val thread = Thread {
            val getPrefs = PreferenceManager.getDefaultSharedPreferences(baseContext)
            val isFirstStart = getPrefs.getBoolean("firstStart", true)
            if (isFirstStart) {
                val i = Intent(this@MainActivity, FilmyIntro::class.java)
                startActivity(i)
                val e = getPrefs.edit()
                e.putBoolean("firstStart", false)
                e.apply()
            }
        }
        thread.start()
    }

    fun cantProceed(status: Int) {
        Handler().postDelayed({
            if (trendingFragment != null && !trendingFragment!!.isShowingFromDatabase) {
                cantProceed = true
                binding.tabLayout.visibility = View.GONE
                //binding.viewpager.visibility = View.GONE
                //binding.mainErrorView.visibility = View.VISIBLE
                disableToolbarScrolling()
            }
        }, 1000)
    }

    private fun canProceed() {
        cantProceed = false
        binding.tabLayout.visibility = View.VISIBLE
        //binding.viewpager.visibility = View.VISIBLE
        //binding.mainErrorView.visibility = View.GONE
        //trendingFragment?.retryLoading()
        enableToolbarScrolling()
    }

    private fun disableToolbarScrolling() {
        val params = binding.toolbarScroller.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
    }

    private fun enableToolbarScrolling() {
        val params = binding.toolbarScroller.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        trendingFragment = Trending()
        val inTheatersFragment = InTheaters()
        val upComingFragment = UpComing()

        val adapter = CollectionsPagerAdapter(supportFragmentManager)
        adapter.addFragment(trendingFragment!!, getString(R.string.trending))
        // adapter.addFragment(inTheatersFragment, getString(R.string.theatres))
        // adapter.addFragment(upComingFragment, getString(R.string.upcoming))
        viewPager.adapter = adapter
    }

    private fun getSearchedResult(query: String) {
        searchFragment.getSearchedResult(query)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val itemSearch = menu.findItem(R.id.action_search)
        val itemAccount = menu.findItem(R.id.ic_collections)

        if (nightMode) {
            itemSearch.setIcon(R.drawable.ic_action_action_search)
            itemAccount.setIcon(R.drawable.ic_action_collections_bookmark2)
        }

        binding.searchView.setMenuItem(itemSearch)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
                    binding.searchView.setQuery(searchWrd, false)
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (binding.searchView.isSearchOpen) {
            binding.searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mMessageReceiver, IntentFilter("fetch-failed"))
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onPause()
    }
}