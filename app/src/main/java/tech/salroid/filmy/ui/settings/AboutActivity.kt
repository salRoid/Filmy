package tech.salroid.filmy.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityAboutBinding
import tech.salroid.filmy.utility.themeSystemBars
import androidx.core.net.toUri

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeSystemBars(lightStatusBar = true)

        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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

    fun sendEmail(view: View) {
        when (view.id) {
            R.id.email_webianks -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = "mailto: webianks@gmail.com".toUri()
                startActivity(Intent.createChooser(emailIntent, "Send feedback"))
            }

            R.id.email_salroid -> {
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = "mailto: gupta.sajal631@gmail.com".toUri()
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
        intent.launchUrl(this, url.toUri())
    }
}