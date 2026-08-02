package tech.salroid.filmy.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.Preference.OnPreferenceClickListener
import tech.salroid.filmy.R
import tech.salroid.filmy.utility.FilmyUtility.startSharingIntent

class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        setupThemePreference()

        val licenseKey: String = requireActivity().getString(R.string.license_key)
        val shareKey: String = requireActivity().getString(R.string.share_key)
        val aboutKey: String = requireActivity().getString(R.string.about_key)

        (findPreference(licenseKey) as? Preference)?.onPreferenceClickListener =
            OnPreferenceClickListener {
                startActivity(Intent(requireContext(), LicenseActivity::class.java))
                true
            }

        (findPreference(shareKey) as? Preference)?.onPreferenceClickListener =
            OnPreferenceClickListener {
                startSharingIntent(requireContext())
                true
            }

        (findPreference(aboutKey) as? Preference)?.onPreferenceClickListener =
            OnPreferenceClickListener {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
                true
            }
    }

    private fun setupThemePreference() {
        val themeKey: String = requireActivity().getString(R.string.theme_key)

        // Theme Values
        val modeNightNo: String = requireActivity().getString(R.string.mode_night_no)
        val modeNightYes: String = requireActivity().getString(R.string.mode_night_yes)

        // Summary Text For Theme
        val light = requireActivity().getString(R.string.summary_light)
        val dark = requireActivity().getString(R.string.summary_dark)
        val systemDefault = requireActivity().getString(R.string.summary_system_default)

        // Theme Changing Preference
        (findPreference(themeKey) as? ListPreference)?.run {
            val selectedMode = when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_NO -> light
                AppCompatDelegate.MODE_NIGHT_YES -> dark
                else -> systemDefault
            }
            summary = selectedMode

            onPreferenceChangeListener = OnPreferenceChangeListener { _, value ->
                val nightMode = when (value) {
                    modeNightNo -> {
                        summary = light
                        AppCompatDelegate.MODE_NIGHT_NO
                    }
                    modeNightYes -> {
                        summary = dark
                        AppCompatDelegate.MODE_NIGHT_YES
                    }
                    else -> {
                        summary = systemDefault
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                }
                AppCompatDelegate.setDefaultNightMode(nightMode)
                true
            }
        }
    }
}