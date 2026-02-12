package tech.salroid.filmy.utility

import android.content.Context
import androidx.preference.PreferenceManager
import tech.salroid.filmy.R
import androidx.core.content.edit

object PreferenceHelper {

    private const val COLD_START = "coldStart"
    const val SESSION_ID = "sessionID"

    fun isColdStart(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(COLD_START, true)

    fun setColdStartDone(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putBoolean(COLD_START, false)
            }
    }

    fun getCurrentThemeMode(context: Context): String? {
        val themeKey = context.getString(R.string.theme_key)
        val modeNightFollowSystem = context.getString(R.string.mode_night_follow_system)
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(themeKey, modeNightFollowSystem)
    }

    fun setThemeMode(context: Context, mode: String) {
        val themeKey = context.getString(R.string.theme_key)
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(themeKey, mode)
            }
    }
}