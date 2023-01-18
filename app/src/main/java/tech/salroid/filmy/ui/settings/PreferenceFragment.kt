package tech.salroid.filmy.ui.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.Preference.OnPreferenceClickListener
import tech.salroid.filmy.R

class PreferenceFragment : PreferenceFragmentCompat() {

    private var imagePref: SwitchPreferenceCompat? = null
    private var darkPref: SwitchPreferenceCompat? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
        val myPreference =
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()

        imagePref = findPreference("imagequality") as? SwitchPreferenceCompat
        imagePref?.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference, o ->
                val quality: String
                val switchPreference = preference as SwitchPreferenceCompat
                quality = if (!switchPreference.isChecked) "original" else "w1280"
                myPreference.putString("image_quality", quality)
                myPreference.apply()
                true
            }

        darkPref = findPreference("darkMode") as? SwitchPreferenceCompat
        darkPref?.onPreferenceChangeListener = OnPreferenceChangeListener { _, _ ->
            recreateActivity()
            true
        }

        val license = findPreference("license") as? Preference
        license?.onPreferenceClickListener = OnPreferenceClickListener {
            startActivity(Intent(activity, LicenseActivity::class.java))
            true
        }

        val share = findPreference("Share") as? Preference
        share?.onPreferenceClickListener = OnPreferenceClickListener {
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

        val about = findPreference("About") as? Preference
        about?.onPreferenceClickListener = OnPreferenceClickListener {
            startActivity(Intent(activity, AboutActivity::class.java))
            true
        }
    }

    private fun recreateActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            activity?.recreate()
        }, 200)
    }
}