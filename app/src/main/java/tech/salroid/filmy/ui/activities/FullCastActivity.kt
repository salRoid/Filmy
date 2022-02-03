package tech.salroid.filmy.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.adapters.CastAdapter
import tech.salroid.filmy.data.local.model.Cast
import tech.salroid.filmy.databinding.ActivityFullCastBinding

class FullCastActivity : AppCompatActivity() {

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
        binding.fullCastRecycler.layoutManager = LinearLayoutManager(this@FullCastActivity)

        val castList = intent?.getSerializableExtra("cast_list") as? List<Cast>
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent?.getStringExtra("toolbar_title")

        val fullCastAdapter = castList?.let {
            CastAdapter(it, false) { castData, _, _ ->
                val intent = Intent(this, CharacterDetailsActivity::class.java)
                intent.putExtra("id", castData.id.toString())
                startActivity(intent)
            }
        }

        binding.fullCastRecycler.adapter = fullCastAdapter
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}