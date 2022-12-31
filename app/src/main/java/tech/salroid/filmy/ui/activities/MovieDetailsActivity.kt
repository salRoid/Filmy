package tech.salroid.filmy.ui.activities

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.FilmyDbHelper
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.RatingResponse
import tech.salroid.filmy.data.network.NetworkUtil
import tech.salroid.filmy.databinding.ActivityDetailedBinding
import tech.salroid.filmy.ui.animations.RevealAnimation
import tech.salroid.filmy.ui.fragment.*
import tech.salroid.filmy.utility.FilmyUtility.getStatusBarHeight
import tech.salroid.filmy.utility.FilmyUtility.getToolBarHeight
import tech.salroid.filmy.utility.makeStatusBarTransparent
import tech.salroid.filmy.utility.showSnackBar
import tech.salroid.filmy.utility.toReadableDate
import java.text.DecimalFormat

class MovieDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private var isWatchlist: Boolean = false
    private var isFavourite: Boolean = false
    private var trailerArray = mutableListOf<String?>()
    private var trailerArrayName = mutableListOf<String?>()
    private var movieImdbId: String? = null
    private lateinit var movieDetails: MovieDetails

    private var networkApplicable = false
    private var databaseApplicable = false
    private var savedDatabaseApplicable = false
    private var trailerBoolean = false
    private var type = 0

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

    private lateinit var castFragment: CastFragment
    private lateinit var crewFragment: CrewFragment
    private lateinit var similarFragment: SimilarFragment
    private lateinit var allTrailerFragment: AllTrailerFragment
    private lateinit var fullReadFragment: FullReadFragment

    private var nightMode = false
    private var movieRatingAudience: String? = null
    private var movieRatingMetaScore: String? = null
    private var movieTitleHyphen: String? = null
    private lateinit var binding: ActivityDetailedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        nightMode = sp.getBoolean("dark", false)
        if (nightMode) setTheme(R.style.DetailsActivityThemeDark) else setTheme(R.style.DetailsActivityTheme)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!nightMode) allThemeLogic() else nightModeLogic()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val pref = PreferenceManager.getDefaultSharedPreferences(this@MovieDetailsActivity)
        quality = pref.getString("image_quality", "original")

        makeStatusBarTransparent()
        updateToolBarScrims()
        setupListeners()
        getDataFromIntent(intent)

        if (savedInstanceState == null) {
            RevealAnimation.performReveal(binding.motionLayout)
            performDataFetching()
        }

        showCastFragment()
        showCrewFragment()
        showSimilarFragment()
    }

    private fun setupListeners() {
        binding.headerContainer.setOnClickListener(this)
        binding.backdrop.setOnClickListener(this)
        binding.trailorView.setOnClickListener(this)
        binding.youtubeIconContainer.setOnClickListener(this)
    }

    private fun updateToolBarScrims() {
        val toolBarScrimHeight = getStatusBarHeight(this) + getToolBarHeight(this)
        val toolbarScrimStartParams =
            binding.toolBarScrimStart.layoutParams as ViewGroup.LayoutParams
        val toolbarScrimEndParams = binding.toolBarScrimEnd.layoutParams as ViewGroup.LayoutParams
        toolbarScrimStartParams.height = toolBarScrimHeight
        toolbarScrimEndParams.height = toolBarScrimHeight

        binding.backdrop.viewTreeObserver.addOnScrollChangedListener {
            val rect = Rect()
            binding.backdrop.let {
                it.getLocalVisibleRect(rect)
                val heightPixels = it.height + rect.top - rect.bottom
                var heightPercentage = (100 - ((heightPixels.toDouble() / it.height) * 100)).toInt()
                if (rect.top < 0) heightPercentage = 0

                val visibilityModifier = (heightPercentage.toDouble() / 100).toFloat()
                binding.toolBarScrimStart.alpha = visibilityModifier
                binding.toolBarScrimEnd.alpha = 1.0f - visibilityModifier
            }
        }
    }

    private fun getDataFromIntent(intent: Intent?) {
        intent?.let {
            networkApplicable = it.getBooleanExtra("network_applicable", false)
            databaseApplicable = it.getBooleanExtra("database_applicable", false)
            savedDatabaseApplicable = it.getBooleanExtra("saved_database_applicable", false)
            type = it.getIntExtra("type", 0)
            movieId = it.getStringExtra("id")
            movieTitle = it.getStringExtra("title")
        }
    }

    private fun nightModeLogic() {
        binding.motionLayout.setBackgroundColor(Color.parseColor("#121212"))
        binding.headerContainer.setBackgroundColor(Color.parseColor("#212121"))
        binding.viewExtraInfo.extraDetails.setBackgroundColor(Color.parseColor("#212121"))
        binding.viewRatings.ratingBar.setBackgroundColor(Color.parseColor("#212121"))
    }

    private fun allThemeLogic() {
        binding.motionLayout.setBackgroundColor(Color.parseColor("#E0E0E0"))
        binding.headerContainer.setBackgroundColor(resources.getColor(R.color.primaryColor))
        binding.viewExtraInfo.extraDetails.setBackgroundColor(resources.getColor(R.color.primaryColor))
        binding.viewRatings.ratingBar.setBackgroundColor(resources.getColor(R.color.primaryColor))
    }

    override fun onResume() {
        super.onResume()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val nightModeNew = sp.getBoolean("dark", false)
        if (nightMode != nightModeNew) recreate()

        performDataFetching()
    }

    private fun performDataFetching() {
        // Get details from DB
        lifecycleScope.launch(Dispatchers.IO) {
            val movieDetails = movieId?.toInt()?.let {
                FilmyDbHelper
                    .getDb(applicationContext)
                    .movieDetailsDao()
                    .getDetailsOfType(it, type)
            }
            lifecycleScope.launch(Dispatchers.Main) {
                movieDetails?.let {
                    showMovieDetails(it)
                    isWatchlist = it.watchlist
                    isFavourite = it.favorite
                    invalidateOptionsMenu()
                }
            }

            if (networkApplicable) {
                movieId?.let {
                    NetworkUtil.getMovieDetails(it, { movieDetailsResponse ->
                        movieDetailsResponse?.let { it1 ->
                            showMovieDetails(it1)
                            if (databaseApplicable) {
                                saveMovieDetailsInDb(it1)
                            }
                        }
                    }, {
                    })
                }
            }
        }

        if (!databaseApplicable && !savedDatabaseApplicable) {
            binding.main.visibility = View.INVISIBLE
            binding.breathingProgress.visibility = View.VISIBLE
        }
    }

    private fun saveMovieDetailsInDb(
        movieDetails: MovieDetails,
        addedDueToCollection: Boolean = false,
        msg: String? = null
    ) {
        movieDetails.watchlist = isWatchlist
        movieDetails.favorite = isFavourite

        if (addedDueToCollection) {
            if (msg == "watchlist") movieDetails.watchlist = true
            if (msg == "favorites") movieDetails.favorite = true
        }

        lifecycleScope.launch(Dispatchers.IO) {
            movieDetails.type = type
            FilmyDbHelper
                .getDb(applicationContext)
                .movieDetailsDao()
                .insert(movieDetails)

            if (addedDueToCollection) {
                lifecycleScope.launch(Dispatchers.Main) {
                    if (msg == "watchlist") isWatchlist = true
                    if (msg == "favorites") isFavourite = true
                    binding.backdrop.showSnackBar("Movie added to $msg")
                    invalidateOptionsMenu()
                }
            }
        }
    }

    private fun updateMovieDetailsInDb(movieDetails: MovieDetails, msg: String, remove: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            movieDetails.type = type
            val updatedID = FilmyDbHelper
                .getDb(applicationContext)
                .movieDetailsDao()
                .updateDetails(movieDetails)

            lifecycleScope.launch(Dispatchers.Main) {
                if (updatedID > 0) {
                    if (remove) {
                        if (msg == "watchlist") isWatchlist = false
                        if (msg == "favorites") isFavourite = false

                        binding.backdrop.showSnackBar("Movie removed from $msg", positive = false)
                    } else {
                        if (msg == "watchlist") isWatchlist = true
                        if (msg == "favorites") isFavourite = true
                        binding.backdrop.showSnackBar("Movie added to $msg")
                    }

                    invalidateOptionsMenu()
                } else {
                    // Movie is not in db but as it's going to be in watchlist/fav
                    // we should save the all movie details
                    saveMovieDetailsInDb(movieDetails, addedDueToCollection = true, msg = msg)
                }
            }
        }
    }

    private fun showCastFragment() {
        castFragment = CastFragment.newInstance(null, movieTitle)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.cast_container, castFragment)
            .commit()
    }

    private fun showCrewFragment() {
        crewFragment = CrewFragment.newInstance(null, movieTitle)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.crew_container, crewFragment)
            .commit()
    }

    private fun showSimilarFragment() {
        similarFragment = SimilarFragment.newInstance(null, movieTitle)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.similar_container, similarFragment)
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

        youTubeTrailers?.let {
            if (youTubeTrailers.size != 0) {
                var mainTrailer = true

                for (i in 0 until youTubeTrailers.size) {
                    val singleTrailer = youTubeTrailers[i]
                    trailerArray.add(singleTrailer.source)
                    trailerArrayName.add(singleTrailer.name)

                    val type = singleTrailer.type
                    if (mainTrailer) {
                        if (type == "Trailer") {
                            trailor = singleTrailer.source
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
            //String videoId = extractYoutubeId(trailer);
            trailerThumbnailUrl = (resources.getString(R.string.trailer_img_prefix) + trailor
                    + resources.getString(R.string.trailer_img_suffix))
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
        movieImdbId?.let { it ->
            NetworkUtil.getRating(it, { ratings ->
                ratings?.let { it1 ->
                    setRating(it1)
                }
            }, {

            })
        }

        // Get Cast, Crew and Similar Movies
        movieIdFinal?.let {
            castFragment.getCastAndCrew(it) { crews ->
                crewFragment.showCrews(ArrayList(crews))
            }
            similarFragment.getSimilarMovies(it)
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
            R.id.action_fav ->  if (isFavourite) removeFavorite() else addFavorite()
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
            var smallTitle = movieTitleHyphen?.toLowerCase()
            smallTitle = smallTitle?.replace("[^0-9-a-z]".toRegex(), "")
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
        updateMovieDetailsInDb(movieDetails, "watchlist", false)
    }

    private fun addFavorite() {
        movieDetails.favorite = true
        updateMovieDetailsInDb(movieDetails, "favorites", false)
    }

    private fun removeWatchlist() {
        movieDetails.watchlist = false
        updateMovieDetailsInDb(movieDetails, "watchlist", true)
    }

    private fun removeFavorite() {
        movieDetails.favorite = false
        updateMovieDetailsInDb(movieDetails, "favorites", true)
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

    override fun onClick(view: View) {
        when (view.id) {
            R.id.header_container -> if (movieTitle != null && movieDesc != null) {
                fullReadFragment = FullReadFragment().also {
                    it.arguments = Bundle().apply {
                        putString("title", movieTitle)
                        putString("desc", movieDesc)
                    }
                    it.show(supportFragmentManager, "DESC")
                }
            }
            R.id.backdrop -> if (bannerForFullScreen != null) {
                val intent = Intent(this@MovieDetailsActivity, FullScreenImage::class.java)
                intent.putExtra("img_url", bannerForFullScreen)
                startActivity(intent)
            }
            R.id.trailorView -> {
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
            R.id.youtube_icon_container -> if (trailerBoolean) {
                allTrailerFragment = AllTrailerFragment()
                val args = Bundle()
                args.putString("title", movieTitle)
                args.putStringArray("trailers", trailerArray.toTypedArray())
                args.putStringArray("trailers_name", trailerArrayName.toTypedArray())
                allTrailerFragment.arguments = args
                supportFragmentManager.beginTransaction()
                    .replace(R.id.motionLayout, allTrailerFragment)
                    .addToBackStack("TRAILER").commit()
            }
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