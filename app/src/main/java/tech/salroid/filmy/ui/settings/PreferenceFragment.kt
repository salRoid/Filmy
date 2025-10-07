package tech.salroid.filmy.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import tech.salroid.filmy.R

class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        val myPreference = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()

        val imagePref = findPreference<SwitchPreferenceCompat>("imagequality")
        imagePref?.setOnPreferenceChangeListener { preference, o ->
            val quality: String
            val switchPreference = preference as SwitchPreferenceCompat
            quality = if (!switchPreference.isChecked) "original" else "w1280"
            myPreference.putString("image_quality", quality)
            myPreference.apply()
            true
        }

        val themePreference = findPreference<ListPreference>("theme")
        themePreference?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue) {
                "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            activity?.recreate()
            true
        }

        val license = findPreference<Preference>("license")
        license?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(activity, LicenseActivity::class.java))
            true
        }

        val share = findPreference<Preference>("Share")
        share?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val appShareDetails = resources.getString(R.string.app_share_link)
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plain"
            myIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Check out this awesome movie app.\n*filmy*\n$appShareDetails"
            )
            startActivity(Intent.createChooser(myIntent, "Share with"))
            true
        }

        val about = findPreference<Preference>("About")
        about?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(activity, AboutActivity::class.java))
            true
        }
    }
}