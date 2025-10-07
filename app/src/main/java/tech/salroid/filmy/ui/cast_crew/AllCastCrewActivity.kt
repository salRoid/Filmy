package tech.salroid.filmy.ui.cast_crew

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastCrew
import tech.salroid.filmy.databinding.ActivityFullCastBinding
import tech.salroid.filmy.ui.adapters.CastCrewAdapter
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.CAST_CREW_LIST
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.TOOLBAR_TITLE
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.MEMBER_ID

class AllCastCrewActivity : AppCompatActivity() {

    private var nightMode = false
    private lateinit var binding: ActivityFullCastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        nightMode = isDarkMode()
        if (nightMode) setTheme(R.style.AppTheme_MD3_Dark) else setTheme(R.style.AppTheme_MD3)

        super.onCreate(savedInstanceState)
        binding = ActivityFullCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = " "
        binding.title.text = intent?.getStringExtra(TOOLBAR_TITLE)

        val castCrewList =
            intent?.getParcelableArrayListExtra<CastCrew>(CAST_CREW_LIST) as? ArrayList<CastCrew>
        val adapter = castCrewList?.let {
            CastCrewAdapter(it, false) { castCrew, _, _ ->
                val id = when (castCrew) {
                    is CastCrew.CastData -> castCrew.cast.id
                    is CastCrew.CrewData -> castCrew.crew.id
                }
                val intent = Intent(this, CastCrewDetailsActivity::class.java)
                intent.putExtra(MEMBER_ID, id.toString())
                startActivity(intent)
            }
        }

        binding.recyclerView.adapter = adapter
        if (nightMode) allThemeLogic()
    }

    private fun allThemeLogic() {
        binding.title.setTextColor(Color.parseColor("#bdbdbd"))
    }

    private fun isDarkMode(): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themeValue = preferences.getString("theme", "system")

        return when (themeValue) {
            "light" -> false
            "dark" -> true
            else -> { // system
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (nightMode != isDarkMode()) recreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}