package tech.salroid.filmy.ui.cast_crew

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastCrewDetailsResponse
import tech.salroid.filmy.data.local.model.CastMovie
import tech.salroid.filmy.data.local.model.CastCrewMoviesResponse
import tech.salroid.filmy.databinding.ActivityDetailedCastBinding
import tech.salroid.filmy.ui.full.AllMoviesActivity
import tech.salroid.filmy.ui.adapters.MemberMoviesAdapter
import tech.salroid.filmy.ui.details.MovieDetailsActivity
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.MEMBER_ID
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.MOVIES
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment.Companion.TOOLBAR_TITLE
import tech.salroid.filmy.ui.full.FullReadFragment
import tech.salroid.filmy.ui.full.FullReadFragment.Companion.DESCRIPTION
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.FROM_ACTIVITY
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.NETWORK_APPLICABLE
import tech.salroid.filmy.utility.PreferenceHelper.isDarkModeEnabled
import tech.salroid.filmy.utility.toReadableDate

@AndroidEntryPoint
class CastCrewDetailsActivity : AppCompatActivity() {

    private val viewModel: CastCrewViewModel by viewModels()
    private var characterId: String? = null
    private var characterTitle: String? = null
    private var moviesList: ArrayList<CastMovie>? = null
    private var characterBio: String? = null
    private var nightMode = false
    private lateinit var binding: ActivityDetailedCastBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        nightMode = isDarkModeEnabled(this)
        if (nightMode) setTheme(R.style.AppTheme_MD3_Dark) else setTheme(R.style.AppTheme_MD3)

        super.onCreate(savedInstanceState)
        binding = ActivityDetailedCastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        binding.more.setOnClickListener {
            if (!(moviesList == null && characterTitle == null)) {
                val intent = Intent(this@CastCrewDetailsActivity, AllMoviesActivity::class.java)
                intent.putExtra(MOVIES, moviesList)
                intent.putExtra(TOOLBAR_TITLE, characterTitle)
                startActivity(intent)
            }
        }

        binding.characterMovies.layoutManager = GridLayoutManager(this@CastCrewDetailsActivity, 3)
        binding.characterMovies.isNestedScrollingEnabled = false

        binding.overviewContainer.setOnClickListener {
            if (characterTitle != null && characterBio != null) {
                val fragment = FullReadFragment.newInstance(characterTitle, characterBio)
                fragment.show(supportFragmentManager, DESCRIPTION)
            }
        }

        characterId = intent?.getStringExtra(MEMBER_ID)
        observeUiState()

        characterId?.let {
            viewModel.getCastCrewDetails(it)
            viewModel.getCastCrewMovies(it)
        }
    }

    private fun observeUiState() {
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

    override fun onResume() {
        super.onResume()
        if (nightMode != isDarkModeEnabled(this)) recreate()
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
            binding.birthPlace.visibility = View.GONE
        }

        if (details.biography?.isEmpty() == true) {
            binding.overviewContainer.visibility = View.GONE
            binding.overview.visibility = View.GONE
        } else {
            if (Build.VERSION.SDK_INT >= 24) {
                binding.overview.text = Html.fromHtml(details.biography, Html.FROM_HTML_MODE_LEGACY)
            }
        }

        Glide.with(this)
            .load(getString(R.string.member_profile_url_2, details.profilePath))
            .fitCenter().into(binding.displayProfile)
    }

    private fun showPersonMovies(castMovieDetails: CastCrewMoviesResponse) {
        val charAdapter =
            MemberMoviesAdapter(castMovieDetails.castMovies, true) { movie, _ ->
                val intent = Intent(this, MovieDetailsActivity::class.java)
                intent.putExtra(MOVIE_ID, movie.id.toString())
                intent.putExtra(MOVIE_TITLE, movie.title)
                intent.putExtra(NETWORK_APPLICABLE, true)
                intent.putExtra(FROM_ACTIVITY, false)
                startActivity(intent)
            }

        binding.characterMovies.adapter = charAdapter
        if (castMovieDetails.castMovies.size > 5) {
            binding.more.visibility = View.VISIBLE
        } else {
            binding.more.visibility = View.INVISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            supportFinishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(DESCRIPTION) as FullReadFragment?
        if (fragment != null && fragment.isVisible) {
            fragment.dismiss()
        } else {
            super.onBackPressed()
        }
    }
}