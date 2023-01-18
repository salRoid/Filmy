package tech.salroid.filmy.utility

import android.content.Context
import androidx.preference.PreferenceManager

object PreferenceHelper {
    private const val DARK_MODE_ON = "darkMode"
    private const val COLD_START = "coldStart"

    fun isColdStart(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(COLD_START, true)

    fun setColdStartDone(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(COLD_START, false)
            .apply()
    }

    fun isDarkModeEnabled(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(DARK_MODE_ON, false)

    fun setDarkModeEnabled(context: Context, enabled: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(DARK_MODE_ON, enabled)
            .apply()
    }
}