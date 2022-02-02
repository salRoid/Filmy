package tech.salroid.filmy.ui.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityDevelopersBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDevelopersBinding
    private var nightMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        binding = ActivityDevelopersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        val typeface = ResourcesCompat.getFont(this, R.font.rubik)
        binding.logo.typeface = typeface

        if (nightMode) allThemeLogic()
        setBannerAndProfile()
    }

    private fun setBannerAndProfile() {
        Glide.with(this).load(getString(R.string.profile_webianks))
            .into((findViewById<View>(R.id.profile_webianks) as ImageView))
        Glide.with(this).load(getString(R.string.profile_salroid))
            .into((findViewById<View>(R.id.profile_salroid) as ImageView))
        Glide.with(this).load(getString(R.string.banner_webianks))
            .into((findViewById<View>(R.id.banner_webianks) as ImageView))
        Glide.with(this).load(getString(R.string.banner_salroid))
            .into((findViewById<View>(R.id.banner_salroid) as ImageView))
    }

    private fun allThemeLogic() {
        binding.logo.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun sendEmail(view: View) {
        when (view.id) {
            R.id.email_webianks -> {
                val webianksEmailIntent = Intent(Intent.ACTION_SENDTO)
                webianksEmailIntent.data = Uri.parse("mailto: webianks@gmail.com")
                startActivity(Intent.createChooser(webianksEmailIntent, "Send feedback"))
            }
            R.id.email_salroid -> {
                val salroidEmailIntent = Intent(Intent.ACTION_SENDTO)
                salroidEmailIntent.data = Uri.parse("mailto: gupta.sajal631@gmail.com")
                startActivity(Intent.createChooser(salroidEmailIntent, "Send feedback"))
            }
        }
    }

    fun redirectGithub(view: View) {
        when (view.id) {
            R.id.github_webianks -> {
                openCustomTabIntent(getString(R.string.git_webianks), R.color.black)
            }
            R.id.github_salroid -> {
                openCustomTabIntent(getString(R.string.git_salroid), R.color.black)
            }
        }
    }

    fun redirectWebsite(view: View) {
        when (view.id) {
            R.id.website_webianks -> {
                openCustomTabIntent(getString(R.string.website_webianks), R.color.colorAccent)
            }
            R.id.website_salroid -> {
                openCustomTabIntent(getString(R.string.website_salroid), R.color.colorAccent)
            }
        }
    }

    private fun openCustomTabIntent(url: String, color: Int) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this@AboutActivity, color))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
    }
}