package tech.salroid.filmy.ui.full

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastMovie
import tech.salroid.filmy.databinding.ActivityFullMovieBinding
import tech.salroid.filmy.ui.adapters.MemberMoviesAdapter
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.MOVIES
import tech.salroid.filmy.ui.details.MovieDetailsActivity
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.TOOLBAR_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.FROM_ACTIVITY
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.NETWORK_APPLICABLE
import tech.salroid.filmy.utility.PreferenceHelper.isDarkModeEnabled

class AllMoviesActivity : AppCompatActivity() {

    private var movieList: List<CastMovie>? = null
    private var nightMode = false
    private lateinit var binding: ActivityFullMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        nightMode = isDarkModeEnabled(this)
        if (nightMode) setTheme(R.style.AppTheme_MD3_Dark) else setTheme(R.style.AppTheme_MD3)

        super.onCreate(savedInstanceState)
        binding = ActivityFullMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.fullMovieRecycler.layoutManager = GridLayoutManager(this@AllMoviesActivity, 3)
        movieList = intent?.getSerializableExtra(MOVIES) as List<CastMovie>

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = " "
        binding.title.text = intent?.getStringExtra(TOOLBAR_TITLE)

        movieList?.let {
            val movieAdapter =
                MemberMoviesAdapter(it, false) { movie, _ ->
                    val intent = Intent(this, MovieDetailsActivity::class.java)
                    intent.putExtra(MOVIE_ID, movie.id.toString())
                    intent.putExtra(MOVIE_TITLE, movie.title)
                    intent.putExtra(NETWORK_APPLICABLE, true)
                    intent.putExtra(FROM_ACTIVITY, false)
                    startActivity(intent)
                }
            binding.fullMovieRecycler.adapter = movieAdapter
        }

        if (nightMode) allThemeLogic()
    }

    private fun allThemeLogic() {
        binding.title.setTextColor(Color.parseColor("#bdbdbd"))
    }

    override fun onResume() {
        super.onResume()
        if (nightMode != isDarkModeEnabled(this)) recreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}