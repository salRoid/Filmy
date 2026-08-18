package tech.salroid.filmy.utility

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import tech.salroid.filmy.R
import androidx.core.content.edit
import java.util.Locale
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

object PreferenceHelper {

    private const val COLD_START = "coldStart"
    const val SESSION_ID = "sessionID"
    const val COUNTRY_KEY = "selectedCountry"

    fun getSelectedCountry(context: Context): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString(COUNTRY_KEY, null)
            ?: Locale.getDefault().country.takeIf { it.isNotBlank() }
            ?: "US"

    private fun SharedPreferences.resolveSelectedCountry(): String =
        getString(COUNTRY_KEY, null)
            ?: Locale.getDefault().country.takeIf { it.isNotBlank() }
            ?: "US"

    // Emits the current region whenever it changes, so reactive pipelines
    // (e.g. Movies/Shows paging) can rebuild when the user changes region
    // in Account settings, instead of only reading it once at request time.
    fun SharedPreferences.selectedCountryFlow(): Flow<String> = callbackFlow {
        trySend(resolveSelectedCountry())
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == COUNTRY_KEY) {
                trySend(prefs.resolveSelectedCountry())
            }
        }
        registerOnSharedPreferenceChangeListener(listener)
        awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    fun setSelectedCountry(context: Context, countryCode: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(COUNTRY_KEY, countryCode)
            }
    }

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