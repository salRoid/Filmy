package tech.salroid.filmy.activities

import androidx.appcompat.app.AppCompatActivity
import tech.salroid.filmy.custom_adapter.CharacterDetailsActivityAdapter
import android.os.Bundle
import android.preference.PreferenceManager
import tech.salroid.filmy.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.view.MenuItem
import tech.salroid.filmy.parser.CharacterDetailActivityParseWork
import tech.salroid.filmy.data_classes.CharacterDetailsData
import tech.salroid.filmy.databinding.ActivityFullMovieBinding

class FullMovieActivity : AppCompatActivity(), CharacterDetailsActivityAdapter.ClickListener {

    private var movieResult: String? = null
    private var nightMode = false
    private lateinit var binding: ActivityFullMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.AppTheme_Base_Dark) else setTheme(R.style.AppTheme_Base)

        super.onCreate(savedInstanceState)
        binding = ActivityFullMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.fullMovieRecycler.layoutManager = LinearLayoutManager(this@FullMovieActivity)
        movieResult = intent?.getStringExtra("cast_json")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent?.getStringExtra("toolbar_title")


        val par = CharacterDetailActivityParseWork(this, movieResult)
        val charList = par.char_parse_cast()
        val charAdapter = CharacterDetailsActivityAdapter(this, charList, false)
        charAdapter.setClickListener(this)
        binding.fullMovieRecycler.setAdapter(charAdapter)
    }

    override fun itemClicked(setterGetterChar: CharacterDetailsData, position: Int) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra("id", setterGetterChar.char_id)
        intent.putExtra("title", setterGetterChar.char_movie)
        intent.putExtra("network_applicable", true)
        intent.putExtra("activity", false)
        startActivity(intent)
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