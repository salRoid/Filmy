package tech.salroid.filmy.ui.settings

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.themeSystemBars

class LicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeSystemBars(lightStatusBar = true)

        setContent {
            AppTheme {
                LicenseScreen(onBackClick = {
                    onBackPressedDispatcher.onBackPressed()
                })
            }
        }
    }
}