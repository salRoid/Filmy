package tech.salroid.filmy.activities

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.youtube.player.YouTubeStandalonePlayer
import tech.salroid.filmy.R
import tech.salroid.filmy.animations.RevealAnimation
import tech.salroid.filmy.data.MovieDetailsResponse
import tech.salroid.filmy.data.RatingResponse
import tech.salroid.filmy.database.FilmContract
import tech.salroid.filmy.databinding.ActivityDetailedBinding
import tech.salroid.filmy.fragment.*
import tech.salroid.filmy.network.NetworkUtil
import tech.salroid.filmy.utility.Confirmation
import tech.salroid.filmy.utility.Constants
import tech.salroid.filmy.utility.FilmyUtility
import tech.salroid.filmy.utility.NullChecker
import java.util.ArrayList

class MovieDetailsActivity : AppCompatActivity(), View.OnClickListener {

    private var trailerArray = mutableListOf<String?>()
    private var trailerArrayName = mutableListOf<String?>()
    private var movieImdbId: String? = null

    private lateinit var movieMap: MutableMap<String, String?>
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

        val statusBarHeight = FilmyUtility.getStatusBarHeight(this)
        val toolbarParams = binding.toolbar.layoutParams as FrameLayout.LayoutParams
        toolbarParams.setMargins(0, statusBarHeight, 0, 0)

        val pref = PreferenceManager.getDefaultSharedPreferences(this@MovieDetailsActivity)
        quality = pref.getString("image_quality", "original")

        binding.headerContainer.setOnClickListener(this)
        binding.newMain.setOnClickListener(this)
        binding.trailorView.setOnClickListener(this)
        binding.youtubeIconContainer.setOnClickListener(this)

        getDataFromIntent(intent)

        if (savedInstanceState == null) {
            RevealAnimation.performReveal(binding.allDetailsContainer)
            performDataFetching()
        }

        showCastFragment()
        showCrewFragment()
        showSimilarFragment()
    }

    private fun nightModeLogic() {
        binding.allDetailsContainer.setBackgroundColor(Color.parseColor("#121212"))
        binding.headerContainer.setBackgroundColor(Color.parseColor("#212121"))
        binding.viewExtraInfo.extraDetails.setBackgroundColor(Color.parseColor("#212121"))
        binding.viewRatings.ratingBar.setBackgroundColor(Color.parseColor("#212121"))
    }

    private fun allThemeLogic() {
        binding.allDetailsContainer.setBackgroundColor(Color.parseColor("#E0E0E0"))
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

        if (networkApplicable) {
            movieId?.let {
                NetworkUtil.getMovieDetails(it, { movieDetailsResponse ->
                    movieDetailsResponse?.let { it1 ->
                        showMovieDetails(it1)
                    }
                }, {

                })
            }
        }

        /*  if (databaseApplicable) supportLoaderManager.initLoader(
              MovieLoaders.MOVIE_DETAILS_LOADER,
              null,
              this
          )*/

        /*if (savedDatabaseApplicable) supportLoaderManager.initLoader(
            MovieLoaders.SAVED_MOVIE_DETAILS_LOADER,
            null,
            this
        )*/

        if (!databaseApplicable && !savedDatabaseApplicable) {
            binding.main.visibility = View.INVISIBLE
            binding.breathingProgress.visibility = View.VISIBLE
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

    private fun showMovieDetails(movie: MovieDetailsResponse) {
        movieIdFinal = movie.id?.toString()
        movieDesc = movie.overview
        movieTitle = movie.title
        movieImdbId = movie.imdbId
        movieRatingTmdb = movie.voteAverage?.toString()
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

        //det_rating.setText(rating);
        binding.viewExtraInfo.detailRuntime.text = "${movie.runtime} mins"
        binding.viewExtraInfo.detailReleased.text = movie.releaseDate
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
            castFragment.getCastAndCrew(it){ crews ->
                crewFragment.showCrews(ArrayList(crews))
            }
            similarFragment.getSimilarMovies(it)
        }

        movieMap = mutableMapOf()
        movieMap["imdb_id"] = movieIdFinal
        movieMap["title"] = movieTitle
        movieMap["tagline"] = movie.tagline
        movieMap["overview"] = movie.overview
        movieMap["rating"] = movieRating
        movieMap["certification"] = genre
        movieMap["language"] = movie.originalLanguage
        movieMap["released"] = movie.releaseDate
        movieMap["runtime"] = "${movie.runtime} mins"
        movieMap["trailer"] = trailer
        movieMap["banner"] = bannerTop
        movieMap["poster"] = posterPath
        movieMap["trailer_img"] = trailerThumbnailUrl

        binding.main.visibility = View.VISIBLE
        binding.breathingProgress.visibility = View.INVISIBLE
    }

    /*  override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
          val cursorLoader: CursorLoader

          when (id) {
              MovieLoaders.MOVIE_DETAILS_LOADER -> {
                  when (type) {
                      0 -> cursorLoader = CursorLoader(
                          this,
                          FilmContract.MoviesEntry.buildMovieWithMovieId(movieId),
                          MovieProjection.GET_MOVIE_COLUMNS, null, null, null
                      )
                      1 -> cursorLoader = CursorLoader(
                          this,
                          FilmContract.InTheatersMoviesEntry.buildMovieWithMovieId(movieId),
                          MovieProjection.GET_MOVIE_COLUMNS, null, null, null
                      )
                      2 -> cursorLoader = CursorLoader(
                          this,
                          FilmContract.UpComingMoviesEntry.buildMovieWithMovieId(movieId),
                          MovieProjection.GET_MOVIE_COLUMNS, null, null, null
                      )
                      else -> {
                          cursorLoader = CursorLoader(
                              this,
                              FilmContract.MoviesEntry.buildMovieWithMovieId(movieId),
                              MovieProjection.GET_MOVIE_COLUMNS, null, null, null
                          )
                      }
                  }
              }
              MovieLoaders.SAVED_MOVIE_DETAILS_LOADER -> {
                  val selection = FilmContract.SaveEntry.TABLE_NAME +
                          "." + FilmContract.SaveEntry.SAVE_ID + " = ? "
                  val selectionArgs = arrayOf(movieId)
                  cursorLoader = CursorLoader(
                      this, FilmContract.SaveEntry.CONTENT_URI,
                      MovieProjection.GET_SAVE_COLUMNS, selection, selectionArgs, null
                  )
              }
              else -> {
                  cursorLoader = CursorLoader(
                      this,
                      FilmContract.MoviesEntry.buildMovieWithMovieId(movieId),
                      MovieProjection.GET_MOVIE_COLUMNS, null, null, null
                  )
              }
          }
          return cursorLoader
      }*/

    /* override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
         val id = loader.id
         if (id == MovieLoaders.MOVIE_DETAILS_LOADER) {
             fetchMovieDetailsFromCursor(data)
         } else if (id == MovieLoaders.SAVED_MOVIE_DETAILS_LOADER) {
             fetchSavedMovieDetailsFromCursor(data)
         }
     }*/

    private fun fetchSavedMovieDetailsFromCursor(data: Cursor?) {

        if (data != null && data.moveToFirst()) {
            val titleIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_TITLE)
            val bannerIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_BANNER)
            val taglineIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_TAGLINE)
            val descriptionIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_DESCRIPTION)
            val trailerIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_TRAILER)
            val ratingIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_RATING)
            val releasedIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_RATING)
            val runtimeIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_RUNTIME)
            val languageIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_LANGUAGE)
            val certificationIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_CERTIFICATION)
            val idIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_ID)
            val posterLinkIndex = data.getColumnIndex(FilmContract.SaveEntry.SAVE_POSTER_LINK)
            val title = data.getString(titleIndex)
            val bannerUrl = data.getString(bannerIndex)
            val tagline = data.getString(taglineIndex)
            val overview = data.getString(descriptionIndex)

            // As it will be used to show it on YouTube
            trailer = data.getString(trailerIndex)
            val posterLink = data.getString(posterLinkIndex)
            val rating = data.getString(ratingIndex)
            val runtime = data.getString(runtimeIndex)
            val released = data.getString(releasedIndex)
            val certification = data.getString(certificationIndex)
            val language = data.getString(languageIndex)

            movieIdFinal = data.getString(idIndex)
            binding.detailTagline.text = tagline
            binding.detailTitle.text = title
            binding.detailOverview.text = overview
            //det_rating.setText(rating);

            binding.viewExtraInfo.detailRuntime.text = runtime
            binding.viewExtraInfo.detailReleased.text = released
            binding.viewExtraInfo.detailCertification.text = certification
            binding.viewExtraInfo.detailLanguage.text = language
            movieDesc = overview
            //showCentreImgUrl = bannerUrl

            try {
                Glide.with(this)
                    .asBitmap()
                    .load(bannerUrl)
                    .into(object : SimpleTarget<Bitmap?>() {

                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            binding.backdrop.setImageBitmap(resource)
                            Palette.from(resource).generate { palette -> // Use generated instance

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
                                }
                            }
                        }
                    })
            } catch (e: Exception) {
                //Log.d(LOG_TAG, e.getMessage());
            }

            val thumbnail = if (trailor != null) {
                trailerBoolean = true
                (resources.getString(R.string.trailer_img_prefix) + trailor
                        + resources.getString(R.string.trailer_img_prefix))
            } else posterLink

            try {
                Glide.with(this)
                    .asBitmap()
                    .load(thumbnail)
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
                //Log.d(LOG_TAG, e.getMessage());
            }
        }
    }

    private fun fetchMovieDetailsFromCursor(data: Cursor?) {

        if (data != null && data.moveToFirst()) {

            val titleIndex = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TITLE)
            val bannerIndex = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_BANNER)
            val taglineIndex = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TAGLINE)
            val descriptionIndex = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_DESCRIPTION)
            val trailerIndex = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_TRAILER)
            val ratingIndex = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_RATING)
            val releasedIndex = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_RELEASED)
            val runtimeIndex = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_RUNTIME)
            val languageIndex = data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_LANGUAGE)
            val certificationIndex =
                data.getColumnIndex(FilmContract.MoviesEntry.MOVIE_CERTIFICATION)

            val title = data.getString(titleIndex)
            val bannerUrl = data.getString(bannerIndex)
            val tagline = data.getString(taglineIndex)
            val overview = data.getString(descriptionIndex)
            val trailer = data.getString(trailerIndex)
            val rating = data.getString(ratingIndex)
            val runtime = data.getString(runtimeIndex)
            val released = data.getString(releasedIndex)
            val certification = data.getString(certificationIndex)
            val language = data.getString(languageIndex)

            if (NullChecker.isSettable(title)) binding.detailTitle.text = title
            if (NullChecker.isSettable(tagline)) binding.detailTagline.text = tagline
            if (NullChecker.isSettable(overview)) binding.detailOverview.text = overview
            if (runtime != null && runtime != "null mins") binding.viewExtraInfo.detailRuntime.text = runtime
            if (NullChecker.isSettable(released)) binding.viewExtraInfo.detailReleased.text =
                released
            if (NullChecker.isSettable(certification)) binding.viewExtraInfo.detailCertification.text =
                certification
            if (NullChecker.isSettable(language)) binding.viewExtraInfo.detailLanguage.text =
                language

            try {
                Glide.with(this)
                    .asBitmap()
                    .load(bannerUrl)
                    .into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            binding.backdrop.setImageBitmap(resource)
                            Palette.from(resource).generate { pallete ->
                                val swatch = pallete?.vibrantSwatch
                                val trailerSwatch = pallete?.darkVibrantSwatch

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
                    .load(trailer)
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
                //Log.d(LOG_TAG, e.getMessage());
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                if (type == -1) startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.action_share -> shareMovie()
            R.id.action_save -> {
                //val offlineMovies = OfflineMovies(this)
                //offlineMovies.saveMovie(movieMap, movieId, movieIdFinal, Constants.FLAG_OFFLINE)
            }
            R.id.action_fav -> Confirmation.confirmFav(
                this,
                movieMap,
                movieId,
                movieIdFinal,
                Constants.FLAG_FAVORITE
            )
            R.id.action_watch -> Confirmation.confirmWatchlist(
                this,
                movieMap,
                movieId,
                movieIdFinal,
                Constants.FLAG_WATCHLIST
            )
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount == 0) {
            super.onBackPressed()
        } else {
            fragmentManager.popBackStack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.movie_detail_menu, menu)
        menu.findItem(R.id.action_save).isVisible = !savedDatabaseApplicable
        return true
    }

    override fun onClick(view: View) {

        when (view.id) {

            R.id.header_container -> if (movieTitle != null && movieDesc != null) {
                fullReadFragment = FullReadFragment()
                val args = Bundle()
                args.putString("title", movieTitle)
                args.putString("desc", movieDesc)
                fullReadFragment.arguments = args
                supportFragmentManager.beginTransaction()
                    .replace(R.id.all_details_container, fullReadFragment).addToBackStack("DESC")
                    .commit()
            }

            R.id.new_main -> if (bannerForFullScreen != null) {
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
                        getString(R.string.Youtube_Api_Key),
                        trailor,
                        timeMilliSeconds,
                        autoPlay,
                        lightBoxMode
                    )
                )
            }

            R.id.youtube_icon_container -> if (trailerBoolean) {
                allTrailerFragment = AllTrailerFragment()
                // Log.d(TAG, "onClick: "+ Arrays.toString(trailer_array));
                val args = Bundle()
                args.putString("title", movieTitle)
                args.putStringArray("trailers", trailerArray.toTypedArray())
                args.putStringArray("trailers_name", trailerArrayName.toTypedArray())
                allTrailerFragment.arguments = args
                supportFragmentManager.beginTransaction()
                    .replace(R.id.all_details_container, allTrailerFragment)
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
            val tomatoMeterScore = tomatoMeterRating?.substring(0, tomatoMeterRating.length - 1)?.toInt()
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
            if (audienceRating?.toFloat()!! > 3.4) binding.viewRatings.flixterRatingImage.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.popcorn)
            ) else binding.viewRatings.flixterRatingImage.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.spilt)
            )
            binding.viewRatings.flixterRating.text = movieRatingAudience
        }

        if (movieRatingMetaScore == "N/A") binding.viewRatings.layoutMeta.visibility =
            View.GONE
        else {
            var smallTitle = movieTitleHyphen?.toLowerCase()
            smallTitle = smallTitle?.replace("[^0-9-a-z]".toRegex(), "")
            val url = "http://www.metacritic.com/movie/$smallTitle"

            when {
                metaScoreRating?.toInt()!! > 60 -> binding.viewRatings.metaRatingBackground.setBackgroundColor(
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

    fun setRatingGone() {
        binding.viewRatings.ratingBar.visibility = View.GONE
    }

    private fun openCustomTabIntent(url: String, color: Int) {
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this@MovieDetailsActivity, color))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }
}