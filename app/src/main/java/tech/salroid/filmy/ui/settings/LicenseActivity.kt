package tech.salroid.filmy.ui.settings

import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import tech.salroid.filmy.R
import tech.salroid.filmy.databinding.ActivityLicenseBinding
import tech.salroid.filmy.utility.themeSystemBars

class LicenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeSystemBars(lightStatusBar = true)

        binding = ActivityLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setup()
    }

    private fun setup() {
        binding.glide.text = Html.fromHtml(
            getString(R.string.glide),
            Html.FROM_HTML_MODE_LEGACY
        )
        binding.materialsearcview.text =
            Html.fromHtml(
                getString(R.string.materialsearch),
                Html.FROM_HTML_MODE_LEGACY
            )
        binding.appintro.text =
            Html.fromHtml(
                getString(R.string.appintro),
                Html.FROM_HTML_MODE_LEGACY
            )
        binding.crashlytics.text =
            Html.fromHtml(
                getString(R.string.crashlytics),
                Html.FROM_HTML_MODE_LEGACY
            )
    }
}