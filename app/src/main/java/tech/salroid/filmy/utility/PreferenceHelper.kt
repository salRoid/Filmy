package tech.salroid.filmy.utility

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.core.content.edit

object PreferenceHelper {
    private const val COLD_START = "coldStart"

    fun isColdStart(context: Context) =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(COLD_START, true)

    fun setColdStartDone(context: Context) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putBoolean(COLD_START, false)
            }
    }

}