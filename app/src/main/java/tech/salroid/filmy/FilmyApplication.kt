package tech.salroid.filmy

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import tech.salroid.filmy.utility.PreferenceHelper

@HiltAndroidApp
class FilmyApplication : Application() {

    override fun onCreate() {
        when (PreferenceHelper.getCurrentThemeMode(this)) {
            getString(R.string.mode_night_no) -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            getString(R.string.mode_night_yes) -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        super.onCreate()
    }
}