package tech.salroid.filmy.ui.activities

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.preference.PreferenceManager
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityLicenseBinding

class License : AppCompatActivity() {

    private var nightMode = false
    private lateinit var binding: ActivityLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        binding = ActivityLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val typeface = ResourcesCompat.getFont(this, R.font.rubik)
        binding.logo.typeface = typeface
        if (nightMode) allThemeLogic()

        if (Build.VERSION.SDK_INT >= 24) v24Setup() else normalSetup()
    }

    private fun normalSetup() {
        binding.glide.text = Html.fromHtml(getString(R.string.glide))
        binding.materialsearcview.text = Html.fromHtml(getString(R.string.materialsearch))
        binding.appintro.text = Html.fromHtml(getString(R.string.appintro))
        binding.crashlytics.text = Html.fromHtml(getString(R.string.crashlytics))
    }

    @TargetApi(Build.VERSION_CODES.N)
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
        binding.logo.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
    }
}