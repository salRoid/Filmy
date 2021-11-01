package tech.salroid.filmy.activities

import androidx.appcompat.app.AppCompatActivity
import tech.salroid.filmy.R
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.core.content.res.ResourcesCompat
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.preference.CheckBoxPreference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.Preference.OnPreferenceClickListener
import android.content.Intent
import android.graphics.Color
import android.view.MenuItem
import tech.salroid.filmy.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private var nightMode = false
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val sp = PreferenceManager.getDefaultSharedPreferences(this)

        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        binding.logo.typeface = ResourcesCompat.getFont(this, R.font.rubik)
        if (nightMode) allThemeLogic()
        fragmentManager.beginTransaction().replace(R.id.container, MyPreferenceFragment()).commit()
    }

    private fun allThemeLogic() {
        binding.logo.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    class MyPreferenceFragment : PreferenceFragment() {

        private var imagePref: SwitchPreference? = null
        private var darkPref: CheckBoxPreference? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preference)

            val myPreference = PreferenceManager.getDefaultSharedPreferences(activity).edit()
            imagePref = findPreference("imagequality") as SwitchPreference
            imagePref?.onPreferenceChangeListener = OnPreferenceChangeListener { preference, o ->
                val quality: String
                val switchPreference = preference as SwitchPreference
                quality = if (!switchPreference.isChecked) {
                    "original"
                } else {
                    "w1280"
                }
                myPreference.putString("image_quality", quality)
                myPreference.apply()
                true
            }

            darkPref = findPreference("dark") as CheckBoxPreference
            darkPref?.onPreferenceChangeListener = OnPreferenceChangeListener { _, _ ->
                recreateActivity()
                true
            }

            val license = findPreference("license")
            license.onPreferenceClickListener = OnPreferenceClickListener {
                startActivity(Intent(activity, License::class.java))
                true
            }

            val share = findPreference("Share")
            share.onPreferenceClickListener = OnPreferenceClickListener {
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

            val about = findPreference("About")
            about.onPreferenceClickListener = OnPreferenceClickListener {
                startActivity(Intent(activity, AboutActivity::class.java))
                true
            }
        }

        private fun recreateActivity() {
            activity.recreate()
        }
    }
}