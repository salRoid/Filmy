package tech.salroid.filmy.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.adapters.CollectionsPagerAdapter
import tech.salroid.filmy.databinding.ActivityCollectionsBinding
import tech.salroid.filmy.ui.activities.fragment.FavoriteFragment
import tech.salroid.filmy.ui.activities.fragment.WatchListFragment

class CollectionsActivity : AppCompatActivity() {

    private var throughShortcut: Boolean? = null
    private lateinit var binding: ActivityCollectionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val nightMode = pref.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        binding = ActivityCollectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            startActivity(Intent(this@CollectionsActivity, FavoriteFragment::class.java))
        }

        binding.watchlistContainer.setOnClickListener {
            startActivity(Intent(this@CollectionsActivity, WatchListFragment::class.java))
        }
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
        val adapter = CollectionsPagerAdapter(supportFragmentManager)
        adapter.addFragment(FavoriteFragment(), getString(R.string.favorite))
        adapter.addFragment(WatchListFragment(), getString(R.string.watchlist))
        viewPager!!.adapter = adapter

        setupIcons()
    }

    private fun setupIcons() {
        binding.tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_round_favorite_24)
        binding.tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_round_bookmark_added_24)
    }

    private fun allThemeLogic() {
        binding.logo.setTextColor(Color.parseColor("#bdbdbd"))
        binding.tabLayout.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.colorDarkThemePrimary
            )
        )
        binding.tabLayout.setTabTextColors(
            Color.parseColor("#bdbdbd"),
            Color.parseColor("#e0e0e0")
        )
    }

    override fun onBackPressed() {
        if (throughShortcut!!) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        } else super.onBackPressed()
    }
}