package tech.salroid.filmy.ui.full

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.elevation.SurfaceColors
import tech.salroid.filmy.data.local.model.CastMovie
import tech.salroid.filmy.databinding.ActivityFullMovieBinding
import tech.salroid.filmy.ui.adapters.MemberMoviesAdapter
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.MOVIES
import tech.salroid.filmy.ui.details.MovieDetailsActivity
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.TOOLBAR_TITLE
import tech.salroid.filmy.ui.details.TvDetailsActivity
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.FROM_ACTIVITY
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.NETWORK_APPLICABLE
import tech.salroid.filmy.ui.similar_recommendation.SimilarRecommendationFragment.Companion.IS_TV
import tech.salroid.filmy.utility.themeSystemBars

class AllMoviesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullMovieBinding
    private var isTv: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeSystemBars(lightStatusBar = true)
        window.statusBarColor = SurfaceColors.SURFACE_3.getColor(this)

        binding = ActivityFullMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.setBackgroundColor(SurfaceColors.SURFACE_3.getColor(this))
        binding.toolbar.title = intent?.getStringExtra(TOOLBAR_TITLE)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        isTv = intent?.getBooleanExtra(IS_TV, false)
        setupAdapter(intent?.getSerializableExtra(MOVIES) as? List<CastMovie>)
    }

    private fun setupAdapter(movieList: List<CastMovie>?) {
        binding.recyclerView.layoutManager = GridLayoutManager(this@AllMoviesActivity, 3)
        movieList?.let {
            val movieAdapter =
                MemberMoviesAdapter(it, false) { movie, _ ->
                    val detailsClass =
                        if (isTv == true) TvDetailsActivity::class.java else MovieDetailsActivity::class.java
                    val intent = Intent(this, detailsClass)
                    intent.putExtra(MOVIE_ID, movie.id.toString())
                    intent.putExtra(MOVIE_TITLE, movie.title)
                    intent.putExtra(NETWORK_APPLICABLE, true)
                    intent.putExtra(FROM_ACTIVITY, false)
                    startActivity(intent)
                }
            binding.recyclerView.adapter = movieAdapter
        }
    }
}