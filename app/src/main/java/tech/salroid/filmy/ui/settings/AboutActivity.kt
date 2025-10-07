package tech.salroid.filmy.ui.settings

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityAboutBinding
import androidx.core.graphics.toColorInt

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    private var nightMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nightMode = isDarkMode()
        if (nightMode) setTheme(R.style.AppTheme_MD3_Dark) else setTheme(R.style.AppTheme_MD3)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        if (nightMode) allThemeLogic()
        setBannerAndProfile()
    }

    private fun setBannerAndProfile() {
        Glide.with(this)
            .load(getString(R.string.banner_webianks))
            .into(binding.bannerWebianks)

        Glide.with(this)
            .load(getString(R.string.banner_salroid))
            .into(binding.bannerSalroid)

        Glide.with(this)
            .load(getString(R.string.profile_webianks))
            .into(binding.profileWebianks)

        Glide.with(this)
            .load(getString(R.string.profile_salroid))
            .into(binding.profileSalroid)
    }

    private fun allThemeLogic() {
        binding.logo.setTextColor("#bdbdbd".toColorInt())
    }

    private fun isDarkMode(): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themeValue = preferences.getString("theme", "system")

        return when (themeValue) {
            "light" -> false
            "dark" -> true
            else -> { // system
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun sendEmail(view: View) {
        when (view.id) {
            R.id.email_webianks -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse("mailto: webianks@gmail.com")
                startActivity(Intent.createChooser(emailIntent, "Send feedback"))
            }
            R.id.email_salroid -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = Uri.parse("mailto: gupta.sajal631@gmail.com")
                startActivity(Intent.createChooser(emailIntent, "Send feedback"))
            }
        }
    }

    fun redirectGithub(view: View) {
        when (view.id) {
            R.id.github_webianks -> openCustomTabIntent(
                getString(R.string.git_webianks),
                R.color.black
            )
            R.id.github_salroid -> openCustomTabIntent(
                getString(R.string.git_salroid),
                R.color.black
            )
        }
    }

    fun redirectWebsite(view: View) {
        when (view.id) {
            R.id.website_webianks -> openCustomTabIntent(
                getString(R.string.website_webianks),
                R.color.colorMore
            )
            R.id.website_salroid -> openCustomTabIntent(
                getString(R.string.website_salroid),
                R.color.colorMore
            )
        }
    }

    private fun openCustomTabIntent(url: String, color: Int) {
        val intent = CustomTabsIntent.Builder().run {
            val customTabColorSchemeParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(ContextCompat.getColor(this@AboutActivity, color))
                .build()
            setDefaultColorSchemeParams(customTabColorSchemeParams)
            build()
        }
        intent.launchUrl(this, Uri.parse(url))
    }

    override fun onResume() {
        super.onResume()
        if (nightMode != isDarkMode()) recreate()
    }
}