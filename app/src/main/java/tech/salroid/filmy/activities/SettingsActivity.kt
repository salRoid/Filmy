package tech.salroid.filmy.activities

import androidx.appcompat.app.AppCompatActivity
import tech.salroid.filmy.R
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.Preference.OnPreferenceClickListener
import android.content.Intent
import android.graphics.Color
import android.view.MenuItem
import androidx.preference.*
import tech.salroid.filmy.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private var nightMode = false
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        binding.logo.typeface = ResourcesCompat.getFont(this, R.font.rubik)
        if (nightMode) allThemeLogic()

        supportFragmentManager.beginTransaction().replace(R.id.container, MyPreferenceFragment()).commit()
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

    class MyPreferenceFragment : PreferenceFragmentCompat() {

        private var imagePref: SwitchPreferenceCompat? = null
        private var darkPref: CheckBoxPreference? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preference, rootKey)

            val myPreference = PreferenceManager.getDefaultSharedPreferences(activity).edit()

            imagePref = findPreference("imagequality") as? SwitchPreferenceCompat
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

            darkPref = findPreference("dark") as? CheckBoxPreference
            darkPref?.onPreferenceChangeListener = OnPreferenceChangeListener { _, _ ->
                recreateActivity()
                true
            }

            val license = findPreference("license") as? Preference
            license?.onPreferenceClickListener = OnPreferenceClickListener {
                startActivity(Intent(activity, License::class.java))
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
            activity?.recreate()
        }
    }
}