package tech.salroid.filmy.activities

import androidx.appcompat.app.AppCompatActivity
import tech.salroid.filmy.R
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.common.api.GoogleApiClient
import android.os.Bundle
import android.preference.PreferenceManager
import butterknife.ButterKnife
import androidx.core.content.res.ResourcesCompat
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.MenuItem
import tech.salroid.filmy.fragment.Favorite
import tech.salroid.filmy.fragment.WatchList
import com.google.android.gms.appindexing.AppIndex
import tech.salroid.filmy.custom_adapter.MyPagerAdapter
import tech.salroid.filmy.fragment.SavedMovies
import androidx.core.content.ContextCompat
import com.google.android.gms.appindexing.Action
import com.google.android.gms.appindexing.Thing
import tech.salroid.filmy.databinding.ActivityCollectionsBinding

class CollectionsActivity : AppCompatActivity() {

    private var client: GoogleApiClient? = null
    private var throughShortcut: Boolean? = null
    private lateinit var binding: ActivityCollectionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectionsBinding.inflate(layoutInflater)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val nightMode = pref.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        setContentView(binding.root)
        ButterKnife.bind(this)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.title = " "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (nightMode) allThemeLogic()

        throughShortcut = intent.getBooleanExtra("throughShortcut", false)

        val typeface = ResourcesCompat.getFont(this, R.font.rubik)
        binding.logo.typeface = typeface

        setupViewPager(binding.viewpager)
        binding.tabLayout.setupWithViewPager(binding.viewpager)

        binding.favContainer.setOnClickListener {
            startActivity(Intent(this@CollectionsActivity, Favorite::class.java))
        }

        binding.watchlistContainer.setOnClickListener {
            startActivity(Intent(this@CollectionsActivity, WatchList::class.java))
        }

        client = GoogleApiClient.Builder(this).addApi(AppIndex.API).build()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (throughShortcut!!) {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            } else finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = MyPagerAdapter(supportFragmentManager)
        adapter.addFragment(SavedMovies(), getString(R.string.offline))
        adapter.addFragment(Favorite(), getString(R.string.favorite))
        adapter.addFragment(WatchList(), getString(R.string.watchlist))
        viewPager!!.adapter = adapter
    }

    private val indexApiAction: Action
        get() {
            val `object` = Thing.Builder()
                    .setName("Account Page")
                    .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                    .build()
            return Action.Builder(Action.TYPE_VIEW)
                    .setObject(`object`)
                    .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                    .build()
        }

    public override fun onStart() {
        super.onStart()
        client?.connect()
        client?.let { AppIndex.AppIndexApi.start(it, indexApiAction) }
    }

    public override fun onStop() {
        super.onStop()
        client?.let { AppIndex.AppIndexApi.end(it, indexApiAction) }
        client?.disconnect()
    }

    private fun allThemeLogic() {
        binding.logo.setTextColor(Color.parseColor("#bdbdbd"))
        binding.tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorDarkThemePrimary))
        binding.tabLayout.setTabTextColors(Color.parseColor("#bdbdbd"), Color.parseColor("#e0e0e0"))
    }

    override fun onBackPressed() {
        if (throughShortcut!!) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else super.onBackPressed()
    }
}