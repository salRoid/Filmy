package tech.salroid.filmy.ui.cast_crew

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastCrewDetailsResponse
import tech.salroid.filmy.data.local.model.CastMovie
import tech.salroid.filmy.data.local.model.CastCrewMoviesResponse
import tech.salroid.filmy.databinding.ActivityCastDetailsBinding
import tech.salroid.filmy.ui.full.AllMoviesActivity
import tech.salroid.filmy.ui.adapters.MemberMoviesAdapter
import tech.salroid.filmy.ui.details.MovieDetailsActivity
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.MEMBER_ID
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.MOVIES
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.TOOLBAR_TITLE
import tech.salroid.filmy.ui.details.TvDetailsActivity
import tech.salroid.filmy.ui.full.FullReadFragment
import tech.salroid.filmy.ui.full.FullReadFragment.Companion.DESCRIPTION
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.FROM_ACTIVITY
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.NETWORK_APPLICABLE
import tech.salroid.filmy.ui.similar_recommendation.SimilarRecommendationFragment.Companion.IS_TV
import tech.salroid.filmy.utility.themeSystemBars
import tech.salroid.filmy.utility.toReadableDate

@AndroidEntryPoint
class CastCrewDetailsActivity : AppCompatActivity() {

    private val viewModel: CastCrewViewModel by viewModels()
    private var characterId: String? = null
    private var characterTitle: String? = null
    private var moviesList: ArrayList<CastMovie>? = null
    private var characterBio: String? = null
    private lateinit var binding: ActivityCastDetailsBinding
    private var isTv: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeSystemBars(lightStatusBar = true)

        binding = ActivityCastDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        binding.characterMovies.layoutManager = GridLayoutManager(
            this@CastCrewDetailsActivity,
            3
        )

        characterId = intent?.getStringExtra(MEMBER_ID)
        isTv = intent?.getBooleanExtra(IS_TV, false)

        collectUiState()
        characterId?.let {
            viewModel.getCastCrewDetails(it)

            val label = if (isTv == true) {
                viewModel.getCastCrewTvShows(it)
                getString(R.string.tv_shows)
            } else {
                viewModel.getCastCrewMovies(it)
                getString(R.string.movies)
            }
            binding.moviesLabel.text = label
        }
    }

    private fun setupClickListeners() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragment =
                    supportFragmentManager.findFragmentByTag(DESCRIPTION) as FullReadFragment?
                if (fragment != null && fragment.isVisible) {
                    fragment.dismiss()
                } else {
                    if (isEnabled) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.more.setOnClickListener {
            if (!(moviesList == null && characterTitle == null)) {
                val intent = Intent(this@CastCrewDetailsActivity, AllMoviesActivity::class.java)
                intent.putExtra(MOVIES, moviesList)
                intent.putExtra(TOOLBAR_TITLE, characterTitle)
                intent.putExtra(IS_TV, isTv)
                startActivity(intent)
            }
        }

        binding.overview.setOnClickListener {
            if (characterTitle != null && characterBio != null) {
                val fragment = FullReadFragment.newInstance(characterTitle, characterBio)
                fragment.show(supportFragmentManager, DESCRIPTION)
            }
        }
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            viewModel.uiStateCastCrewDetails.collect {
                it?.let {
                    showPersonalDetails(it)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.uiStateCastCrewMovies.collect {
                it?.let {
                    moviesList = it.castMovies
                    showPersonMovies(it)
                }
            }
        }
    }

    private fun showPersonalDetails(details: CastCrewDetailsResponse) {
        characterTitle = details.name
        characterBio = details.biography
        binding.name.text = details.name

        details.birthday?.let {
            binding.dob.text = it.toReadableDate()
        } ?: run {
            binding.dob.visibility = View.GONE
        }

        details.placeOfBirth?.let {
            binding.birthPlace.text = it
        } ?: run {
            binding.birthPlace.visibility = View.INVISIBLE
        }

        if (details.biography?.isEmpty() == true) {
            binding.overview.visibility = View.GONE
        } else {
            binding.overview.text = Html.fromHtml(details.biography, Html.FROM_HTML_MODE_LEGACY)
        }

        Glide.with(this)
            .load(getString(R.string.member_profile_url_2, details.profilePath))
            .fitCenter()
            .into(binding.displayProfile)
    }

    private fun showPersonMovies(castMovieDetails: CastCrewMoviesResponse) {
        val charAdapter =
            MemberMoviesAdapter(castMovieDetails.castMovies, true) { movie, _ ->
                val detailsClass =
                    if (isTv == true) TvDetailsActivity::class.java else MovieDetailsActivity::class.java
                val intent = Intent(this, detailsClass)
                intent.putExtra(MOVIE_ID, movie.id.toString())
                intent.putExtra(MOVIE_TITLE, movie.title)
                intent.putExtra(NETWORK_APPLICABLE, true)
                intent.putExtra(FROM_ACTIVITY, false)
                startActivity(intent)
            }

        binding.characterMovies.adapter = charAdapter
        binding.more.isVisible = castMovieDetails.castMovies.size > 5
    }
}