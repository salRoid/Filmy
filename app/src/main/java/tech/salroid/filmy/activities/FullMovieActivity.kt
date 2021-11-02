package tech.salroid.filmy.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import tech.salroid.filmy.R
import tech.salroid.filmy.adapters.CharacterDetailsActivityAdapter
import tech.salroid.filmy.databinding.ActivityFullMovieBinding
import tech.salroid.filmy.parser.CharacterDetailsActivityParseWork

class FullMovieActivity : AppCompatActivity() {

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


        val par = CharacterDetailsActivityParseWork(movieResult.toString())
        val moviesList = par.parsePersonMovies()
        val movieAdapter =
            CharacterDetailsActivityAdapter(moviesList, false) { personMovieDetail, position ->
                val intent = Intent(this, MovieDetailsActivity::class.java)
                intent.putExtra("id", personMovieDetail.movieId)
                intent.putExtra("title", personMovieDetail.movieTitle)
                intent.putExtra("network_applicable", true)
                intent.putExtra("activity", false)
                startActivity(intent)
            }

        binding.fullMovieRecycler.adapter = movieAdapter
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