package tech.salroid.filmy.ui.details

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.youtube.player.YouTubeStandalonePlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.RatingResponse
import tech.salroid.filmy.data.local.model.TrailerData
import tech.salroid.filmy.databinding.ActivityDetailedBinding
import tech.salroid.filmy.ui.full.FullBannerActivity
import tech.salroid.filmy.ui.home.MainActivity
import tech.salroid.filmy.ui.animations.RevealAnimation
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment
import tech.salroid.filmy.ui.cast_crew.CastCrewViewModel
import tech.salroid.filmy.ui.full.AllTrailersFragment
import tech.salroid.filmy.ui.full.FullBannerActivity.Companion.IMAGE_URL
import tech.salroid.filmy.ui.full.FullReadFragment
import tech.salroid.filmy.ui.full.FullReadFragment.Companion.DESCRIPTION
import tech.salroid.filmy.ui.full.FullReadFragment.Companion.TITLE
import tech.salroid.filmy.ui.home.MoviesFragment
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.DATABASE_APPLICABLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TYPE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.NETWORK_APPLICABLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.SAVED_DATABASE_APPLICABLE
import tech.salroid.filmy.ui.similar.SimilarFragment
import tech.salroid.filmy.ui.similar.SimilarViewModel
import tech.salroid.filmy.utility.FilmyUtility.getStatusBarHeight
import tech.salroid.filmy.utility.FilmyUtility.getToolBarHeight
import tech.salroid.filmy.utility.PreferenceHelper.isDarkModeEnabled
import tech.salroid.filmy.utility.showSnackBar
import tech.salroid.filmy.utility.themeSystemBars
import tech.salroid.filmy.utility.toReadableDate
import java.text.DecimalFormat

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private val viewModel: MovieDetailsViewModel by viewModels()
    private val castViewModel: CastCrewViewModel by viewModels()
    private val similarViewModel: SimilarViewModel by viewModels()

    private var isWatchlist: Boolean = false
    private var isFavourite: Boolean = false

    private lateinit var movieDetails: MovieDetails
    private lateinit var binding: ActivityDetailedBinding
    private val trailers = mutableListOf<TrailerData>()

    private var networkApplicable = false
    private var databaseApplicable = false
    private var savedDatabaseApplicable = false
    private var trailerBoolean = false
    private var type = 0
    private var darkMode = false
    private var movieId: String? = null
    private var trailor: String? = null
    private var trailer: String? = null
    private var movieDesc: String? = null
    private var quality: String? = null
    private var movieTagline: String? = null
    private val movieRating: String? = null
    private var movieRatingTmdb: String? = null
    private var bannerForFullScreen: String? = null
    private var movieTitle: String? = null
    private var movieIdFinal: String? = null
    private var movieRatingAudience: String? = null
    private var movieRatingMetaScore: String? = null
    private var movieTitleHyphen: String? = null
    private var movieImdbId: String? = null

    companion object {
        const val IMAGE_QUALITY = "IMAGE_QUALITY"
        const val IMAGE_QUALITY_DEFAULT = "original"
        const val WATCHLIST = "watchlist"
        const val FAVOURITES = "favorites"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        updateTheme()
        super.onCreate(savedInstanceState)

        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!darkMode) allThemeLogic() else darkModeLogic()

        val pref = PreferenceManager.getDefaultSharedPreferences(this@MovieDetailsActivity)
        quality = pref.getString(IMAGE_QUALITY, IMAGE_QUALITY_DEFAULT)

        updateToolBar()
        themeSystemBars(!darkMode)
        updateToolBarScrims()
        setupListeners()
        getDataFromIntent(intent)

        if (savedInstanceState == null) {
            RevealAnimation.performReveal(binding.motionLayout)
            getMovieDetails()
        }

        collectUiStates()
        showCastFragment()
        showCrewFragment()
        showSimilarFragment()
    }

    private fun collectUiStates() {
        lifecycleScope.launch {
            viewModel.uiStateMovieDetails.collect {
                it?.let {
                    showMovieDetails(it)
                    isWatchlist = it.watchlist
                    isFavourite = it.favorite
                    invalidateOptionsMenu()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiStateAddToCollection.collect { (addedToCollection, message) ->
                if (addedToCollection) {
                    if (message == WATCHLIST) isWatchlist = true
                    if (message == FAVOURITES) isFavourite = true
                    binding.backdrop.showSnackBar("Movie added to $message")
                    invalidateOptionsMenu()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.uiStateUpdateCollection.collect { (updatedID, message, remove) ->
                if (updatedID > 0) {
                    if (remove) {
                        if (message == WATCHLIST) isWatchlist = false
                        if (message == FAVOURITES) isFavourite = false

                        binding.backdrop.showSnackBar(
                            "Movie removed from $message",
                            positive = false
                        )
                    } else {
                        if (message == WATCHLIST) isWatchlist = true
                        if (message == FAVOURITES) isFavourite = true
                        binding.backdrop.showSnackBar("Movie added to $message")
                    }

                    invalidateOptionsMenu()
                } else if (updatedID != -1) {
                    // Movie is not in db but as it's going to be in watchlist/fav
                    // we should save the all movie details
                    viewModel.saveMovieDetailsInDb(
                        movieDetails,
                        addedToCollection = true,
                        message = message
                    )
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiStateRatingResponse.collect {
                it?.let {
                    setRating(it)
                }
            }
        }
    }

    private fun updateToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun updateTheme() {
        darkMode = isDarkModeEnabled(this)
        if (darkMode) setTheme(R.style.AppTheme_MD3_Dark_Details) else setTheme(R.style.AppTheme_MD3_Details)
    }

    private fun setupListeners() {
        binding.headerContainer.setOnClickListener {
            if (movieTitle != null && movieDesc != null) {
                FullReadFragment().also {
                    it.arguments = Bundle().apply {
                        putString(TITLE, movieTitle)
                        putString(DESCRIPTION, movieDesc)
                    }
                    it.show(supportFragmentManager, DESCRIPTION)
                }
            }
        }
        binding.backdrop.setOnClickListener {
            if (bannerForFullScreen != null) {
                val intent = Intent(this@MovieDetailsActivity, FullBannerActivity::class.java)
                intent.putExtra(IMAGE_URL, bannerForFullScreen)
                startActivity(intent)
            }
        }
        binding.trailorView.setOnClickListener {
            val timeMilliSeconds = 0
            val autoPlay = true
            val lightBoxMode = false
            if (trailerBoolean) startActivity(
                YouTubeStandalonePlayer.createVideoIntent(
                    this@MovieDetailsActivity,
                    BuildConfig.YOUTUBE_API_KEY,
                    trailor,
                    timeMilliSeconds,
                    autoPlay,
                    lightBoxMode
                )
            )
        }
        binding.youtubeIconContainer.setOnClickListener {
            if (trailerBoolean) {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.motionLayout, AllTrailersFragment.newInstance(
                            movieTitle,
                            trailers.toTypedArray()
                        )
                    )
                    .addToBackStack(AllTrailersFragment.TRAILERS)
                    .commit()
            }
        }
    }

    private fun updateToolBarScrims() {
        val toolBarScrimHeight = getStatusBarHeight(this) + getToolBarHeight(this)
        val toolbarScrimStartParams =
            binding.toolBarScrimStart.layoutParams as ViewGroup.LayoutParams
        val toolbarScrimEndParams =
            binding.toolBarScrimEnd.layoutParams as ViewGroup.LayoutParams
        toolbarScrimStartParams.height = toolBarScrimHeight
        toolbarScrimEndParams.height = toolBarScrimHeight

        binding.backdrop.viewTreeObserver.addOnScrollChangedListener {
            val rect = Rect()
            binding.backdrop.let {
                it.getLocalVisibleRect(rect)
                val heightPixels = it.height + rect.top - rect.bottom
                var heightPercentage =
                    (100 - ((heightPixels.toDouble() / it.height) * 100)).toInt()
                if (rect.top < 0) heightPercentage = 0

                val visibilityModifier = (heightPercentage.toDouble() / 100).toFloat()
                binding.toolBarScrimStart.alpha = visibilityModifier
                binding.toolBarScrimEnd.alpha = 1.0f - visibilityModifier
            }
        }
    }

    private fun getDataFromIntent(intent: Intent?) {
        intent?.let {
            networkApplicable = it.getBooleanExtra(NETWORK_APPLICABLE, false)
            databaseApplicable = it.getBooleanExtra(DATABASE_APPLICABLE, false)
            savedDatabaseApplicable = it.getBooleanExtra(SAVED_DATABASE_APPLICABLE, false)
            val movieType = it.getSerializableExtra(MOVIE_TYPE) as? MoviesFragment.MovieType
            type = movieType?.ordinal ?: 0
            movieId = it.getStringExtra(MOVIE_ID)
            movieTitle = it.getStringExtra(MOVIE_TITLE)
        }
    }

    private fun darkModeLogic() {
        binding.motionLayout.setBackgroundColor(Color.parseColor("#121212"))
        binding.headerContainer.setBackgroundColor(Color.parseColor("#212121"))
        binding.viewExtraInfo.extraDetails.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.surfaceColorDark
            )
        )
        binding.viewRatings.ratingBar.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.surfaceColorDark
            )
        )
    }

    private fun allThemeLogic() {
        binding.motionLayout.setBackgroundColor(Color.parseColor("#E0E0E0"))
        binding.headerContainer.setBackgroundColor(resources.getColor(R.color.primaryColor))
        binding.viewExtraInfo.extraDetails.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.surfaceColorLight
            )
        )
        binding.viewRatings.ratingBar.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.surfaceColorLight
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (darkMode != isDarkModeEnabled(this)) recreate()
        getMovieDetails()
    }

    private fun getMovieDetails() {
        if (!databaseApplicable && !savedDatabaseApplicable) {
            binding.main.visibility = View.INVISIBLE
            binding.breathingProgress.visibility = View.VISIBLE
        }

        viewModel.getMovieDetails(movieId, type)
    }

    private fun showCastFragment() {
        val castFragment =
            CastCrewFragment.newInstance(null, movieTitle, CastCrewFragment.CastCrewType.CAST)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.cast_container, castFragment)
            .commit()
    }

    private fun showCrewFragment() {
        val crewFragment =
            CastCrewFragment.newInstance(null, movieTitle, CastCrewFragment.CastCrewType.CREW)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.crew_container, crewFragment)
            .commit()
    }

    private fun showSimilarFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.similar_container, SimilarFragment.newInstance(null, movieTitle))
            .commit()
    }

    private fun showMovieDetails(movie: MovieDetails) {
        this.movieDetails = movie
        movieIdFinal = movie.id.toString()
        movieDesc = movie.overview
        movieTitle = movie.title
        movieImdbId = movie.imdbId
        movieRatingTmdb = movie.voteAverage?.let {
            DecimalFormat("#.#").format(it)
        }
        movieTitleHyphen = movieTitle?.replace(' ', '-')
        movieTagline = movie.tagline

        // Generes
        var genre = ""
        val genreArray = movie.genres
        for (i in 0 until genreArray.size) {
            if (i > 3) break
            val finalGenre = genreArray[i].name
            var punctuation = ", "
            if (i == genre.length) punctuation = ""
            genre = genre + punctuation + finalGenre
        }

        // Banners
        val posterPrefix500 = resources.getString(R.string.poster_prefix_500)
        val posterPrefixAddQuality = resources.getString(R.string.poster_prefix_add_quality)

        val bannerTop: String
        val bannerPath = movie.backdropPath
        val posterPath = movie.posterPath

        if (bannerPath != "null") {
            bannerTop = posterPrefix500 + bannerPath
            bannerForFullScreen = posterPrefixAddQuality + quality + bannerPath
        } else {
            bannerTop = posterPrefix500 + posterPath
            bannerForFullScreen = posterPrefixAddQuality + quality + posterPath
        }

        // Trailers
        val youTubeTrailers = movie.trailers?.youtube
        trailers.clear()
        youTubeTrailers?.let {
            if (youTubeTrailers.size != 0) {
                var mainTrailer = true
                youTubeTrailers.forEach {
                    trailers.add(TrailerData(it.name, it.source))
                    if (mainTrailer) {
                        if (it.type == "Trailer") {
                            trailor = it.source
                            mainTrailer = false
                        } else trailor = youTubeTrailers[0].source
                    }
                }
                trailer = resources.getString(R.string.trailer_link_prefix) + trailor
            } else trailer = null
        }

        val trailerThumbnailUrl: String
        if (trailor != null) {
            trailerBoolean = true
            trailerThumbnailUrl = getString(R.string.trailer_img_url, trailor)
        } else {
            trailerThumbnailUrl = resources.getString(R.string.poster_prefix_185) + posterPath
        }

        binding.detailTagline.text = movie.tagline
        binding.detailTitle.text = movie.title
        binding.detailOverview.text = movie.overview

        //det_rating.setText(rating)
        binding.viewExtraInfo.detailRuntime.text = "${movie.runtime} mins"
        binding.viewExtraInfo.detailReleased.text = movie.releaseDate?.toReadableDate()
        binding.viewExtraInfo.detailCertification.text = genre
        binding.viewExtraInfo.detailLanguage.text = movie.originalLanguage

        if (movie.tagline != "") binding.detailTagline.visibility = View.VISIBLE

        try {
            Glide.with(this)
                .asBitmap()
                .load(bannerTop)
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {

                        binding.backdrop.setImageBitmap(resource)
                        Palette.from(resource).generate { palette ->
                            val swatch = palette?.vibrantSwatch
                            val trailerSwatch = palette?.darkVibrantSwatch

                            if (swatch != null) {
                                binding.header.setBackgroundColor(swatch.rgb)
                                binding.detailTitle.setTextColor(swatch.titleTextColor)
                                binding.detailTagline.setTextColor(swatch.bodyTextColor)
                                binding.detailOverview.setTextColor(swatch.bodyTextColor)
                            }
                            if (trailerSwatch != null) {
                                binding.trailorBackground.setBackgroundColor(trailerSwatch.rgb)
                                binding.youtubeIcon.setColorFilter(
                                    trailerSwatch.bodyTextColor,
                                    PorterDuff.Mode.SRC_IN
                                )
                                binding.moreTv.setTextColor(trailerSwatch.bodyTextColor)
                            }
                        }
                    }
                })
        } catch (e: Exception) {
            //Log.d(LOG_TAG, e.getMessage());
        }

        try {
            Glide.with(this)
                .asBitmap()
                .load(trailerThumbnailUrl)
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        binding.detailYoutube.setImageBitmap(resource)
                        if (trailerBoolean) binding.playButton.visibility = View.VISIBLE
                    }
                })
        } catch (e: Exception) {
        }

        // Get Ratings
        viewModel.getRatings(movieImdbId)

        // Get Cast, Crew and Similar Movies
        movieIdFinal?.let {
            castViewModel.getCastAndCrew(it)
            similarViewModel.getSimilar(it)
        }

        binding.main.visibility = View.VISIBLE
        binding.breathingProgress.visibility = View.INVISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                if (type == -1) startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.action_share -> shareMovie()
            R.id.action_fav -> if (isFavourite) removeFavorite() else addFavorite()
            R.id.action_watch -> if (isWatchlist) removeWatchlist() else addWatchlist()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setRating(rating: RatingResponse) {
        val imdbRating = rating.imdbRating
        var tomatoMeterRating = rating.tomatoRating
        val audienceRating = rating.tomatoUserRating
        val metaScoreRating = rating.metascore
        val image = rating.tomatoImage
        val rottenTomatoPage = rating.tomatoURL

        // Above TomatoMeter does not work this does
        val ratingArray = rating.ratings
        for (i in 0 until ratingArray.size) {
            if (ratingArray[i].source == "Rotten Tomatoes") {
                tomatoMeterRating = ratingArray[i].value
            }
        }

        movieRatingAudience = audienceRating
        movieRatingMetaScore = metaScoreRating

        if (imdbRating == "N/A") {
            binding.viewRatings.layoutImdb.visibility = View.GONE
        } else {
            binding.viewRatings.imdbRating.text = imdbRating
            binding.viewRatings.layoutImdb.setOnClickListener {
                openCustomTabIntent(
                    resources.getString(R.string.imdb_link_prefix) + movieImdbId,
                    R.color.imdbYellow
                )
            }
        }

        if (tomatoMeterRating == "N/A") {
            binding.viewRatings.layoutTomato.visibility = View.GONE
        } else {
            val tomatoMeterScore =
                tomatoMeterRating?.substring(0, tomatoMeterRating.length - 1)?.toInt()
            if (tomatoMeterScore != null) {
                when {
                    tomatoMeterScore > 74 -> binding.viewRatings.tomatoRatingImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.certified
                        )
                    )
                    tomatoMeterScore > 59 -> binding.viewRatings.tomatoRatingImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.fresh
                        )
                    )
                    tomatoMeterScore < 60 -> binding.viewRatings.tomatoRatingImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this, R.drawable.rotten
                        )
                    )
                }
            }

            binding.viewRatings.tomatoRating.text = tomatoMeterRating
            binding.viewRatings.layoutTomato.setOnClickListener {
                openCustomTabIntent(rottenTomatoPage.toString(), R.color.tomatoRed)
            }
        }

        if (movieRatingAudience == "N/A") binding.viewRatings.layoutFlixi.visibility =
            View.GONE else {
            audienceRating?.let {
                if (audienceRating.toFloat() > 3.4) binding.viewRatings.flixterRatingImage.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.popcorn)
                ) else binding.viewRatings.flixterRatingImage.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.spilt)
                )
            }
            binding.viewRatings.flixterRating.text = movieRatingAudience
        }

        if (movieRatingMetaScore == "N/A") binding.viewRatings.layoutMeta.visibility =
            View.GONE
        else {
            var smallTitle = movieTitleHyphen?.lowercase()
            smallTitle = smallTitle?.replace("[^\\d-a-z]".toRegex(), "")
            val url = "http://www.metacritic.com/movie/$smallTitle"

            if (metaScoreRating != null) {
                when {
                    metaScoreRating.toInt() > 60 -> binding.viewRatings.metaRatingBackground.setBackgroundColor(
                        Color.parseColor("#66cc33")
                    )
                    metaScoreRating.toInt() in 41..60 -> binding.viewRatings.metaRatingBackground.setBackgroundColor(
                        Color.parseColor("#ffcc33")
                    )
                    else -> binding.viewRatings.metaRatingBackground.setBackgroundColor(
                        Color.parseColor(
                            "#ff0000"
                        )
                    )
                }
            }

            binding.viewRatings.metaRating.text = movieRatingMetaScore
            binding.viewRatings.metaRatingView.text = movieRatingMetaScore
            binding.viewRatings.layoutMeta.setOnClickListener {
                openCustomTabIntent(url, R.color.metaBlack)
            }
        }

        if (movieRatingTmdb == "0") binding.viewRatings.layoutTmdb.visibility = View.GONE else {
            binding.viewRatings.tmdbRating.text = movieRatingTmdb
            binding.viewRatings.layoutTmdb.setOnClickListener {
                openCustomTabIntent(
                    "https://www.themoviedb.org/movie/$movieId-$movieTitleHyphen",
                    R.color.tmdbGreen
                )
            }
        }
    }

    private fun addWatchlist() {
        movieDetails.watchlist = true
        movieDetails.type = type
        viewModel.updateMovieDetailsInDb(movieDetails, WATCHLIST, false)
    }

    private fun addFavorite() {
        movieDetails.favorite = true
        movieDetails.type = type
        viewModel.updateMovieDetailsInDb(movieDetails, FAVOURITES, false)
    }

    private fun removeWatchlist() {
        movieDetails.watchlist = false
        movieDetails.type = type
        viewModel.updateMovieDetailsInDb(movieDetails, WATCHLIST, true)
    }

    private fun removeFavorite() {
        movieDetails.favorite = false
        movieDetails.type = type
        viewModel.updateMovieDetailsInDb(movieDetails, FAVOURITES, true)
    }

    fun setRatingGone() {
        binding.viewRatings.ratingBar.visibility = View.GONE
    }

    private fun openCustomTabIntent(url: String, color: Int) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this@MovieDetailsActivity, color))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.movie_detail_menu, menu)
        val itemFavorite = menu.findItem(R.id.action_fav)
        val itemWatchlist = menu.findItem(R.id.action_watch)

        if (isFavourite) {
            itemFavorite.setIcon(R.drawable.ic_round_favorite_24)
        }
        if (isWatchlist) {
            itemWatchlist.setIcon(R.drawable.ic_round_bookmark_added_24)
        }
        return true
    }

    private fun shareMovie() {
        val movieImdb = resources.getString(R.string.imdb_link_prefix) + movieImdbId
        if (!(movieTitle == null && movieRating == "null" && movieImdbId == "null")) {
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plain"
            myIntent.putExtra(Intent.EXTRA_TEXT, "*$movieTitle*\n$movieTagline\n$movieImdb\n")
            startActivity(Intent.createChooser(myIntent, "Share with"))
        }
    }
}