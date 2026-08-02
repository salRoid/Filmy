package tech.salroid.filmy.ui.details

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.*
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.elevation.SurfaceColors
import tech.salroid.filmy.ui.full.YoutubePlayerActivity
import tech.salroid.filmy.ui.full.YoutubePlayerActivity.Companion.VIDEO_ID
import tech.salroid.filmy.ui.full.YoutubePlayerActivity.Companion.VIDEO_TITLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.RatingResponse
import tech.salroid.filmy.data.local.model.Review
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.data.local.model.TrailerData
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.databinding.ActivityDetailsBinding
import tech.salroid.filmy.databinding.ReviewItemLayoutBinding
import tech.salroid.filmy.ui.full.FullBannerActivity
import tech.salroid.filmy.ui.animations.RevealAnimation
import tech.salroid.filmy.ui.cast_crew.CastCrewFragment
import tech.salroid.filmy.ui.cast_crew.CastCrewViewModel
import tech.salroid.filmy.ui.full.AllTrailersFragment
import tech.salroid.filmy.ui.full.FullBannerActivity.Companion.IMAGE_URL
import tech.salroid.filmy.ui.full.FullReadFragment
import tech.salroid.filmy.ui.full.FullReadFragment.Companion.DESCRIPTION
import tech.salroid.filmy.ui.full.FullReadFragment.Companion.TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.DATABASE_APPLICABLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_ID
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TITLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.MOVIE_TYPE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.NETWORK_APPLICABLE
import tech.salroid.filmy.ui.home.MoviesFragment.Companion.SAVED_DATABASE_APPLICABLE
import tech.salroid.filmy.ui.similar_recommendation.SimilarRecommendationFragment
import tech.salroid.filmy.ui.similar_recommendation.SimilarRecommendationFragment.Companion.TAG_RECOMMENDATION
import tech.salroid.filmy.ui.similar_recommendation.SimilarRecommendationFragment.Companion.TAG_SIMILAR
import tech.salroid.filmy.ui.similar_recommendation.SimilarRecommendationViewModel
import tech.salroid.filmy.utility.*
import tech.salroid.filmy.utility.FilmyUtility.getNavigationBarHeight

@AndroidEntryPoint
class MovieDetailsActivity : AppCompatActivity() {

    private val viewModel: MovieDetailsViewModel by viewModels()
    private val castViewModel: CastCrewViewModel by viewModels()
    private val similarViewModel: SimilarRecommendationViewModel by viewModels()

    private var isWatchlist: Boolean = false
    private var isFavourite: Boolean = false
    private var visibilityModifier = 0.0f

    private lateinit var movieDetails: MovieDetails
    private lateinit var binding: ActivityDetailsBinding
    private val trailers = mutableListOf<TrailerData>()

    private var networkApplicable = false
    private var databaseApplicable = false
    private var savedDatabaseApplicable = false
    private var trailerBoolean = false
    private var type = 0
    private var movieId: String? = null
    private var trailor: String? = null
    private var trailer: String? = null
    private var movieDesc: String? = null
    private var movieTagline: String? = null
    private val movieRating: String? = null
    private var bannerForFullScreen: String? = null
    private var movieTitle: String? = null
    private var movieIdFinal: String? = null
    private var movieRatingAudience: String? = null
    private var movieRatingMetaScore: String? = null
    private var movieTitleHyphen: String? = null
    private var movieImdbId: String? = null

    companion object {
        const val IMAGE_QUALITY_DEFAULT = "original"
        const val WATCHLIST = "watchlist"
        const val WATCHED = "watched"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        themeSystemBars(isFullScreen = true, transparentStatus = true)

        updateToolBar()
        updateToolBarScrims()
        setupListeners()
        getDataFromIntent(intent)

        if (savedInstanceState == null) {
            RevealAnimation.performReveal(binding.detailsRoot)
            getMovieDetails()
        }

        collectUiStates()
        showCastFragment()
        showCrewFragment()
        showSuggestionFragment(
            R.id.similarContainer,
            TAG_SIMILAR,
            SimilarRecommendationFragment.SuggestionType.SIMILAR
        )
        showSuggestionFragment(
            R.id.recommendationContainer,
            TAG_RECOMMENDATION,
            SimilarRecommendationFragment.SuggestionType.RECOMMENDATION
        )
        setupNavSpace()
    }

    private fun setupNavSpace() {
        binding.root.doOnLayout {
            (binding.navSpace.layoutParams as MarginLayoutParams).height =
                getNavigationBarHeight(this)
        }
    }

    private fun collectUiStates() {
        lifecycleScope.launch {
            viewModel.uiStateMovieDetails.collect {
                it?.let {
                    showMovieDetails(it)
                    isWatchlist = it.watchlist
                    isFavourite = it.watched
                    updateOptionsMenu()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiStateAddToCollection.collect { (addedToCollection, message) ->
                if (addedToCollection) {
                    if (message == WATCHLIST) isWatchlist = true
                    if (message == WATCHED) isFavourite = true
                    binding.backdrop.showSnackBar("Movie added to $message")
                    updateOptionsMenu()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.uiStateUpdateCollection.collect { (updatedID, message, remove) ->
                if (updatedID > 0) {
                    if (remove) {
                        if (message == WATCHLIST) isWatchlist = false
                        if (message == WATCHED) isFavourite = false

                        binding.backdrop.showSnackBar(
                            "Movie removed from $message",
                            positive = false
                        )
                    } else {
                        if (message == WATCHLIST) isWatchlist = true
                        if (message == WATCHED) isFavourite = true
                        binding.backdrop.showSnackBar("Movie added to $message")
                    }

                    updateOptionsMenu()
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

        lifecycleScope.launch {
            viewModel.uiStateReviewResponse.collect {
                it?.let {
                    setReviews(it)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiStateWatchProvidersResponse.collect {
                it?.let {
                    setWatchProviderInfo(it)
                }
            }
        }
    }

    private fun updateToolBar() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount == 0) {
                    if (isEnabled) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                } else {
                    supportFragmentManager.popBackStack()
                }
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_share -> {
                    shareMovie()
                    true
                }
                R.id.action_fav -> {
                    if (isFavourite) removeFavorite() else addFavorite()
                    true
                }
                R.id.action_watch -> {
                    if (isWatchlist) removeWatchlist() else addWatchlist()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupListeners() {
        binding.headerContainer.setOnClickListener {
            showFullReadFragment(movieTitle, movieDesc)
        }
        binding.backdrop.setOnClickListener {
            if (bannerForFullScreen != null) {
                val intent = Intent(this@MovieDetailsActivity, FullBannerActivity::class.java)
                intent.putExtra(IMAGE_URL, bannerForFullScreen)
                startActivity(intent)
            }
        }
        binding.trailorView.setOnClickListener {
            if (trailerBoolean) trailor?.let { videoId ->
                Intent(
                    this@MovieDetailsActivity,
                    YoutubePlayerActivity::class.java
                ).run {
                    putExtra(VIDEO_ID, videoId)
                    putExtra(VIDEO_TITLE, movieTitle)
                    startActivity(this)
                }
            }
        }
        binding.youtubeIconContainer.setOnClickListener {
            if (trailerBoolean) {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.detailsRoot,
                        AllTrailersFragment.newInstance(
                            movieTitle,
                            trailers.toTypedArray()
                        ),
                        AllTrailersFragment.TRAILERS
                    )
                    .addToBackStack(AllTrailersFragment.TRAILERS)
                    .commit()
            }
        }
    }

    private fun updateToolBarScrims() {
        binding.appbar.doOnLayout {
            val toolbarScrimStartParams =
                binding.toolBarScrimStart.layoutParams as ViewGroup.LayoutParams
            val toolbarScrimEndParams =
                binding.toolBarScrimEnd.layoutParams as ViewGroup.LayoutParams
            toolbarScrimStartParams.height = it.height
            toolbarScrimEndParams.height = it.height
        }

        binding.toolBarScrimEnd.setBackgroundColor(SurfaceColors.SURFACE_2.getColor(this))

        binding.backdrop.viewTreeObserver.addOnScrollChangedListener {
            val rect = Rect()
            binding.backdrop.let {
                it.getLocalVisibleRect(rect)
                val heightPixels = it.height + rect.top - rect.bottom
                var heightPercentage =
                    (100 - ((heightPixels.toDouble() / it.height) * 100)).toInt()
                if (rect.top < 0) heightPercentage = 0

                visibilityModifier = (heightPercentage.toDouble() / 100).toFloat()
                binding.toolBarScrimStart.alpha = 2 * visibilityModifier
                binding.toolBarScrimEnd.alpha = 1.0f - visibilityModifier

                val trailerFragment =
                    supportFragmentManager.findFragmentByTag(AllTrailersFragment.TRAILERS)

                if (isDarkThemeActivated() || trailerFragment != null) return@let

                if (visibilityModifier > 0.5f) {
                    binding.toolbar.setNavigationIconTint(
                        ContextCompat.getColor(
                            this,
                            R.color.white
                        )
                    )
                    themeSystemBars(
                        lightStatusBar = false,
                        isFullScreen = true,
                        transparentStatus = true
                    )
                    lightMenuIcons()
                } else {
                    binding.toolbar.setNavigationIconTint(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    themeSystemBars(
                        lightStatusBar = true,
                        isFullScreen = true,
                        transparentStatus = true
                    )
                    darkMenuIcons()
                }
            }
        }
    }

    private fun lightMenuIcons() {
        binding.toolbar.menu.forEach {
            it.icon?.let { it1 ->
                DrawableCompat.setTint(
                    it1,
                    ContextCompat.getColor(this, R.color.white)
                )
            }
        }
    }

    private fun darkMenuIcons() {
        binding.toolbar.menu.forEach {
            it.icon?.let { it1 ->
                DrawableCompat.setTint(
                    it1,
                    ContextCompat.getColor(this, R.color.black)
                )
            }
        }
    }

    private fun getDataFromIntent(intent: Intent?) {
        intent?.let {
            networkApplicable = it.getBooleanExtra(NETWORK_APPLICABLE, false)
            databaseApplicable = it.getBooleanExtra(DATABASE_APPLICABLE, false)
            savedDatabaseApplicable = it.getBooleanExtra(SAVED_DATABASE_APPLICABLE, false)
            val movieType = IntentCompat.getSerializableExtra(
                it, MOVIE_TYPE,
                Movie.MovieType::class.java
            )
            type = movieType?.ordinal ?: 0
            movieId = it.getStringExtra(MOVIE_ID)
            movieTitle = it.getStringExtra(MOVIE_TITLE)
        }
    }

    override fun onResume() {
        super.onResume()

        if (isDarkThemeActivated()) {
            binding.toolbar.setNavigationIconTint(ContextCompat.getColor(this, R.color.white))
            lightMenuIcons()
        }
        getMovieDetails()
    }

    private fun getMovieDetails() {
        if (!this::movieDetails.isInitialized) {
            if (!databaseApplicable && !savedDatabaseApplicable) {
                binding.main.visibility = View.INVISIBLE
                binding.breathingProgress.visibility = View.VISIBLE
            }
           // viewModel.getMovieDetails(movieId, type)
        }
    }

    private fun showCastFragment() {
        val castFragment =
            CastCrewFragment.newInstance(null, movieTitle, CastCrewFragment.CastCrewType.CAST)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.castContainer, castFragment)
            .commit()
    }

    private fun showCrewFragment() {
        val crewFragment =
            CastCrewFragment.newInstance(null, movieTitle, CastCrewFragment.CastCrewType.CREW)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.crewContainer, crewFragment)
            .commit()
    }

    private fun showSuggestionFragment(
        containerId: Int,
        tag: String,
        suggestionType: SimilarRecommendationFragment.SuggestionType
    ) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                containerId,
                SimilarRecommendationFragment.newInstance(null, movieTitle, suggestionType),
                tag
            ).commit()
    }

    private fun showMovieDetails(movie: MovieDetails) {
        this.movieDetails = movie
        movieIdFinal = movie.id.toString()
        movieDesc = movie.overview
        movieTitle = movie.title
        movieImdbId = movie.imdbId
        /*movieRatingTmdb = movie.voteAverage?.let {
            DecimalFormat("#.#").format(it)
        }*/
        movieTitleHyphen = movieTitle?.replace(' ', '-')
        movieTagline = movie.tagline

        // Genres
        val spannedGenres = buildSpannedString {
            for (i in movie.genres.indices) {
                if (i == 3) break
                val genre = movie.genres[i]
                append(genre.name)
                //append("\u00A0") // nbsp
                color(Color.BLACK) {
                    // append(GenreStringer.getEmoji(genre.name))
                }
                if (i < movie.genres.size - 1 && i != 2) append(" / ")
            }
        }

        // Banners
        val posterPrefix500 = resources.getString(R.string.poster_prefix_500)
        val posterPrefixAddQuality = resources.getString(R.string.poster_prefix_add_quality)

        val bannerTop: String
        val bannerPath = movie.backdropPath
        val posterPath = movie.posterPath

        if (bannerPath != "null") {
            bannerTop = posterPrefix500 + bannerPath
            bannerForFullScreen = posterPrefixAddQuality + IMAGE_QUALITY_DEFAULT + bannerPath
        } else {
            bannerTop = posterPrefix500 + posterPath
            bannerForFullScreen = posterPrefixAddQuality + IMAGE_QUALITY_DEFAULT + posterPath
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
            trailerBoolean = false
            trailerThumbnailUrl = resources.getString(R.string.poster_prefix_185) + posterPath
        }

        if (movie.tagline.isNullOrEmpty()) {
            binding.detailTagline.isVisible = false
        } else {
            binding.detailTagline.text = movie.tagline
        }

        binding.detailTitle.text = movie.title
        binding.detailOverview.text = movie.overview

        binding.detailTitle.doOnLayout {
            (binding.posterContainer.layoutParams as? MarginLayoutParams)?.topMargin = when(binding.detailTitle.lineCount) {
                2 -> resources.getDimensionPixelSize(R.dimen.filmy156dp)
                3 -> resources.getDimensionPixelSize(R.dimen.filmy180dp)
                else -> resources.getDimensionPixelSize(R.dimen.filmy132dp)
            }
        }

        val hours = movie.runtime?.div(60)
        val mins = movie.runtime?.rem(60)

        binding.viewExtraInfo.runtime.text = buildSpannedString {
            append("${hours}h")
            append(" ${mins}m")
        }
        binding.viewExtraInfo.released.text = buildSpannedString {
            append("\u00A0") // nbsp
            append("\u00A0") // nbsp
            append("\u2022  ")
            append(movie.releaseDate?.toReadableDate())
        }
        binding.viewExtraInfo.genres.text = spannedGenres
        binding.viewExtraInfo.language.text = buildSpannedString {
            movie.originalLanguage?.let {
                append("\u2022 ")
                append(it)
            }
        }

        Glide.with(this)
            .load(getString(R.string.movie_poster_url, movie.posterPath))
            .into(binding.poster)

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

                                //binding.viewRatings.tmdbRatingBar.trackColor = swatch.bodyTextColor
                                //binding.viewRatings.tmdbRatingBar.setIndicatorColor(swatch.rgb)
                            }

                            if (trailerSwatch != null) {
                                binding.trailerBackground.setBackgroundColor(trailerSwatch.rgb)
                                binding.backdropScrim.setBackgroundColor(trailerSwatch.rgb)
                                binding.youtubeIcon.setColorFilter(
                                    trailerSwatch.bodyTextColor,
                                    PorterDuff.Mode.SRC_IN
                                )
                                binding.moreTv.setTextColor(trailerSwatch.bodyTextColor)
                                /* binding.youtubeIcon.setColorFilter(
                                     trailerSwatch.bodyTextColor,
                                     PorterDuff.Mode.SRC_IN
                                 )*/
                                //binding.viewRatings.ratingLabel.setTextColor(trailerSwatch.bodyTextColor)
                                //binding.viewRatings.tmdbIcon.setTextColor(trailerSwatch.bodyTextColor)
                                //binding.viewRatings.tmdbRatingScore.setTextColor(trailerSwatch.bodyTextColor)
                            }

                            swatch?.let {
                                // binding.watchProviderContainer.setCardBackgroundColor(it.rgb)

                                // binding.watchTitle.setTextColor(it.bodyTextColor)
                                // binding.watchAction.setTextColor(it.titleTextColor)
                                binding.viewExtraInfo.genres.setTextColor(it.bodyTextColor)
                                binding.viewExtraInfo.runtime.setTextColor(it.bodyTextColor)
                                binding.viewExtraInfo.language.setTextColor(it.bodyTextColor)
                                binding.viewExtraInfo.released.setTextColor(it.bodyTextColor)

                                val tintList = ColorStateList.valueOf(it.bodyTextColor)
                                TextViewCompat.setCompoundDrawableTintList(
                                    binding.viewExtraInfo.language,
                                    tintList
                                )
                                TextViewCompat.setCompoundDrawableTintList(
                                    binding.viewExtraInfo.genres,
                                    tintList
                                )
                                TextViewCompat.setCompoundDrawableTintList(
                                    binding.viewExtraInfo.runtime,
                                    tintList
                                )
                                TextViewCompat.setCompoundDrawableTintList(
                                    binding.viewExtraInfo.released,
                                    tintList
                                )
                            }
                        }
                    }
                })
        } catch (e: Exception) {
            //Log.d(LOG_TAG, e.getMessage());
        }

        if (trailerBoolean) {
            binding.trailerContainer.isVisible = true
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
            } catch (_: Exception) {
            }
        } else {
            binding.trailerContainer.isVisible = false
        }

        // Get Ratings
        viewModel.getRatings(movieImdbId)

        // Get Reviews
        //viewModel.getReviews(movieId)

        // Get Watch Providers
        //viewModel.getWatchProviders(movieId)

        // Get Cast, Crew and Similar Movies
        movieIdFinal?.let {
            castViewModel.getCastAndCrew(it)
            similarViewModel.getSimilar(it)
            similarViewModel.getRecommendation(it)
        }

        binding.main.visibility = View.VISIBLE
        binding.breathingProgress.visibility = View.INVISIBLE
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

        // TMDB User Score
        val userScore = movieDetails.voteAverage?.times(10)?.toInt() ?: 0
        binding.viewRatings.tmdbRatingBar.setProgressCompat(userScore, true)
        binding.viewRatings.tmdbRatingScore.text = buildSpannedString {
            append("$userScore% User Score")
        }
        binding.viewRatings.tmdbRatingBy.text = buildSpannedString {
            append(" \u2022 ${movieDetails.voteCount} Ratings")
        }

        //val colorRating = SurfaceColors.SURFACE_1.getColor(this)
        // binding.viewRatings.root.setCardBackgroundColor(colorRating)
        // binding.watchProviderContainer.setCardBackgroundColor(colorRating)
        //binding.trailerContainer.setCardBackgroundColor(colorRating)

        /*   if (imdbRating == "N/A") {
               binding.viewRatings.layoutImdb.visibility = View.GONE
           } else {
               binding.viewRatings.imdbRating.text = imdbRating
               binding.viewRatings.layoutImdb.setOnClickListener {
                   openCustomTabIntent(
                       resources.getString(R.string.imdb_link_prefix) + movieImdbId,
                       R.color.imdbYellow
                   )
               }
           }*/

        /* if (tomatoMeterRating == "N/A") {
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
         }*/

        /* if (movieRatingAudience == "N/A") binding.viewRatings.layoutFlixi.visibility =
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
             val url = "https://www.metacritic.com/movie/$smallTitle"

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
             }*/
        //}


        //if (movieDetails.voteAverage == 0.0) binding.viewRatings.layoutTmdb.visibility = View.GONE else {

        /* binding.viewRatings.layoutTmdb.setOnClickListener {
             openCustomTabIntent(
                 "https://www.themoviedb.org/movie/$movieId-$movieTitleHyphen",
                 R.color.tmdbGreen
             )
         }*/
        //}

        binding.viewExtraInfo.language.text = buildSpannedString {
            rating.language?.let {
                append("\u2022 ")
                append(it)
            }
        }
    }

    private fun setWatchProviderInfo(watchProviderResponse: WatchProviderResponse) {
        val watchProviderStream = watchProviderResponse.results?.IN?.flatrate?.firstOrNull()
        val watchProviderBuy = watchProviderResponse.results?.IN?.buy?.firstOrNull()
        val watchProviderRent = watchProviderResponse.results?.IN?.rent?.firstOrNull()

        binding.watchProviderContainer.isVisible =
            watchProviderStream != null || watchProviderBuy != null || watchProviderRent != null

        val logoPath = watchProviderStream?.logoPath ?: kotlin.run {
            watchProviderBuy?.logoPath ?: kotlin.run {
                watchProviderRent?.logoPath
            }
        }

        Glide.with(this)
            .load(getString(R.string.member_profile_url, logoPath))
            .into(binding.watchProviderLogo)
    }

    private fun setReviews(reviewResponse: ReviewResponse) {
        if (reviewResponse.results.isNotEmpty()) {
            val review = reviewResponse.results.first()
            setSingleReviewData(review, binding.viewReviews1)

            if (reviewResponse.results.size > 1) {
                val review2 = reviewResponse.results[1]
                setSingleReviewData(review2, binding.viewReviews2)
            } else {
                binding.viewReviews2.root.isVisible = false
            }
        } else {
            binding.reviewsLabel.isVisible = false
            binding.viewReviews1.root.isVisible = false
            binding.viewReviews2.root.isVisible = false
        }
    }

    private fun setSingleReviewData(
        review: Review,
        reviewItemLayoutBinding: ReviewItemLayoutBinding
    ) {
        with(reviewItemLayoutBinding) {
            reviewUser.text = review.author
            reviewDate.text = review.createdAt?.toReadableDate()
            reviewContent.text = review.content

            Glide.with(this@MovieDetailsActivity)
                .load(review.authorDetails?.getAvatarUrl(this@MovieDetailsActivity))
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(avatarImage)

            root.setOnClickListener {
                showFullReadFragment(movieTitle, review.content)
            }
        }
    }

    private fun showFullReadFragment(title: String?, description: String?) {
        if (title != null && description != null) {
            FullReadFragment().also {
                it.arguments = Bundle().apply {
                    putString(TITLE, title)
                    putString(DESCRIPTION, description)
                }
                it.show(supportFragmentManager, DESCRIPTION)
            }
        }
    }

    private fun addWatchlist() {
        movieDetails.watchlist = true
        movieDetails.type = type
        viewModel.updateMovieDetailsInDb(movieDetails, WATCHLIST, false)
    }

    private fun addFavorite() {
        movieDetails.watched = true
        movieDetails.type = type
        viewModel.updateMovieDetailsInDb(movieDetails, WATCHED, false)
    }

    private fun removeWatchlist() {
        movieDetails.watchlist = false
        movieDetails.type = type
        viewModel.updateMovieDetailsInDb(movieDetails, WATCHLIST, true)
    }

    private fun removeFavorite() {
        movieDetails.watched = false
        movieDetails.type = type
        viewModel.updateMovieDetailsInDb(movieDetails, WATCHED, true)
    }

    private fun openCustomTabIntent(url: String, color: Int) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this@MovieDetailsActivity, color))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    private fun updateOptionsMenu() {
        val itemFavorite = binding.toolbar.menu.findItem(R.id.action_fav)
        val itemWatchlist = binding.toolbar.menu.findItem(R.id.action_watch)

        val favouriteIcon =
            if (isFavourite) R.drawable.ic_round_favorite_24 else R.drawable.ic_round_favorite_border_24
        val watchlistIcon =
            if (isWatchlist) R.drawable.ic_round_bookmark_added_24 else R.drawable.ic_round_bookmark_add_24

        itemFavorite.setIcon(favouriteIcon)
        itemWatchlist.setIcon(watchlistIcon)

        if (isDarkThemeActivated() || visibilityModifier > 0.5f) {
            lightMenuIcons()
        } else {
            darkMenuIcons()
        }
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