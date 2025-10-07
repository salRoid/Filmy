package tech.salroid.filmy.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityLicenseBinding
import androidx.core.graphics.toColorInt

class LicenseActivity : AppCompatActivity() {

    private var nightMode = false
    private lateinit var binding: ActivityLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nightMode = isDarkMode()
        if (nightMode) setTheme(R.style.AppTheme_MD3_Dark) else setTheme(R.style.AppTheme_MD3)

        binding = ActivityLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        if (nightMode) allThemeLogic()
        v24Setup()
    }

    private fun v24Setup() {
        binding.glide.text = Html.fromHtml(getString(R.string.glide), Html.FROM_HTML_MODE_LEGACY)
        binding.materialsearcview.text =
            Html.fromHtml(getString(R.string.materialsearch), Html.FROM_HTML_MODE_LEGACY)
        binding.appintro.text =
            Html.fromHtml(getString(R.string.appintro), Html.FROM_HTML_MODE_LEGACY)
        binding.crashlytics.text =
            Html.fromHtml(getString(R.string.crashlytics), Html.FROM_HTML_MODE_LEGACY)
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
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (nightMode != isDarkMode()) recreate()
    }
}