package tech.salroid.filmy.ui.home

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.FilmyApp
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.PreferenceHelper

@AndroidEntryPoint
class MainActivityCompose : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { false }
        super.onCreate(savedInstanceState)

        setContent {
            val systemDark = isSystemInDarkTheme()
            val themeMode = PreferenceHelper.getCurrentThemeMode(this)
            
            val darkTheme = when (themeMode) {
                getString(R.string.mode_night_no) -> false
                getString(R.string.mode_night_yes) -> true
                else -> systemDark
            }

            val statusBarStyle =
                if (darkTheme) {
                    SystemBarStyle.dark(Color.Transparent.toArgb())
                } else {
                    SystemBarStyle.light(
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb()
                    )
                }

            enableEdgeToEdge(
                statusBarStyle = statusBarStyle,
                navigationBarStyle = SystemBarStyle.auto(
                    Color.Transparent.toArgb(),
                    Color.Transparent.toArgb(),
                ) { !darkTheme },
            )
            AppTheme(darkTheme = darkTheme) {
                FilmyApp()
            }
        }
    }
}