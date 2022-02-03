package tech.salroid.filmy.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.Crew
import tech.salroid.filmy.databinding.ActivityFullCastBinding
import tech.salroid.filmy.ui.adapters.CrewAdapter

class FullCrewActivity : AppCompatActivity() {

    private var nightMode = false
    private lateinit var binding: ActivityFullCastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        super.onCreate(savedInstanceState)
        binding = ActivityFullCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.fullCastRecycler.layoutManager = LinearLayoutManager(this@FullCrewActivity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra("toolbar_title")

        val crewList = intent?.getSerializableExtra("crew_list") as? ArrayList<Crew>

        val fullCrewAdapter =
            crewList?.let {
                CrewAdapter(it, false) { crewMember, _, _ ->
                    val intent = Intent(this, CharacterDetailsActivity::class.java)
                    intent.putExtra("id", crewMember.id.toString())
                    startActivity(intent)
                }
            }
        binding.fullCastRecycler.adapter = fullCrewAdapter
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}