package tech.salroid.filmy.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import tech.salroid.filmy.R
import androidx.core.content.res.ResourcesCompat
import android.os.Build
import android.text.Html
import android.annotation.TargetApi
import android.graphics.Color
import android.view.MenuItem
import tech.salroid.filmy.databinding.ActivityLicenseBinding

class License : AppCompatActivity() {

    private var nightMode = false
    private lateinit var binding: ActivityLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLicenseBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val sp = PreferenceManager.getDefaultSharedPreferences(this)

        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

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
        binding.circularimageview.text = Html.fromHtml(getString(R.string.circularimageview))
        binding.tatarka.text = Html.fromHtml(getString(R.string.tatarka))
        binding.error.text = Html.fromHtml(getString(R.string.errorview))
        binding.appintro.text = Html.fromHtml(getString(R.string.appintro))
        binding.butterknife.text = Html.fromHtml(getString(R.string.butterknife))
        binding.crashlytics.text = Html.fromHtml(getString(R.string.crashlytics))
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun v24Setup() {
        binding.glide.text = Html.fromHtml(getString(R.string.glide), Html.FROM_HTML_MODE_LEGACY)
        binding.materialsearcview.text =
            Html.fromHtml(getString(R.string.materialsearch), Html.FROM_HTML_MODE_LEGACY)
        binding.circularimageview.text =
            Html.fromHtml(getString(R.string.circularimageview), Html.FROM_HTML_MODE_LEGACY)
        binding.tatarka.text =
            Html.fromHtml(getString(R.string.tatarka), Html.FROM_HTML_MODE_LEGACY)
        binding.error.text =
            Html.fromHtml(getString(R.string.errorview), Html.FROM_HTML_MODE_LEGACY)
        binding.appintro.text =
            Html.fromHtml(getString(R.string.appintro), Html.FROM_HTML_MODE_LEGACY)
        binding.butterknife.text =
            Html.fromHtml(getString(R.string.butterknife), Html.FROM_HTML_MODE_LEGACY)
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