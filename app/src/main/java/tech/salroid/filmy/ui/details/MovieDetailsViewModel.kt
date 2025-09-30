package tech.salroid.filmy.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.RatingResponse
import tech.salroid.filmy.ui.details.MovieDetailsActivity.Companion.FAVOURITES
import tech.salroid.filmy.ui.details.MovieDetailsActivity.Companion.WATCHLIST
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject
import java.net.HttpURLConnection
import java.net.URL

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiStateMovieDetails = MutableStateFlow<MovieDetails?>(null)
    private val _uiStateRatings = MutableStateFlow<RatingResponse?>(null)
    private val _uiStateAddToCollection = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, ""))
    private val _uiStateUpdateCollection = MutableStateFlow(Triple(-1, "", false))

    val uiStateMovieDetails: StateFlow<MovieDetails?> = _uiStateMovieDetails.asStateFlow()
    val uiStateRatingResponse: StateFlow<RatingResponse?> = _uiStateRatings.asStateFlow()
    val uiStateAddToCollection: StateFlow<Pair<Boolean, String?>> =
        _uiStateAddToCollection.asStateFlow()
    val uiStateUpdateCollection: StateFlow<Triple<Int, String, Boolean>> =
        _uiStateUpdateCollection.asStateFlow()

    fun getMovieDetails(movieId: String?, movieType: Int, addToLocal: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            movieId?.toInt()?.let {
                val movieDetails = moviesRepository.getMovieDetailsFromLocal(it, movieType)
                val isWatchList = movieDetails?.watchlist ?: false
                val isFavourite = movieDetails?.favorite ?: false
                _uiStateMovieDetails.emit(movieDetails)

                movieId.let {
                    moviesRepository.getMovieDetailsFromNetwork(it)
                        .flowOn(Dispatchers.IO)
                        .catch { throwable ->
                            throwable.printStackTrace()
                        }.collect { details ->
                            val updatedDetails = details.copy()
                            updatedDetails.watchlist = isWatchList
                            updatedDetails.favorite = isFavourite
                            _uiStateMovieDetails.emit(updatedDetails)

                            if (addToLocal) {
                                saveMovieDetailsInDb(updatedDetails)
                            }
                        }
                }
            }
        }
    }

    fun saveMovieDetailsInDb(
        details: MovieDetails,
        type: Int = 0,
        isWatchlist: Boolean = false,
        isFavourite: Boolean = false,
        addedToCollection: Boolean = false,
        message: String? = null
    ) {
        val movieDetails = details.copy()
        details.watchlist = isWatchlist
        details.favorite = isFavourite

        if (addedToCollection) {
            if (message == WATCHLIST) movieDetails.watchlist = true
            if (message == FAVOURITES) movieDetails.favorite = true
        }

        viewModelScope.launch(Dispatchers.IO) {
            movieDetails.type = type
            moviesRepository.addMovieDetailsToLocal(movieDetails)
            if (addedToCollection) {
                _uiStateAddToCollection.emit(Pair(true, message))
            }
        }
    }

    fun updateMovieDetailsInDb(
        movieDetails: MovieDetails,
        message: String,
        remove: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedID = moviesRepository.updateMovieDetails(movieDetails)
            _uiStateUpdateCollection.emit(
                Triple(
                    updatedID,
                    message,
                    remove
                )
            )
        }
    }

    fun getRatings(ratingID: String?) {
        viewModelScope.launch {
            ratingID?.let {
                moviesRepository.getRatings(it)
                    .flowOn(Dispatchers.IO)
                    .catch {
                        // error
                    }.collect { ratingResponse ->
                        _uiStateRatings.emit(ratingResponse)
                    }
            }
        }
    }

    fun checkIfTrailerIsShort(videoId: String?) {
        if (videoId.isNullOrEmpty()) {
            // _isTrailerAShort.value = false // Or handle as an error/unknown state
            return
        }

        viewModelScope.launch {
            val isShort = isYoutubeShortByUrlCheck(videoId)
            // _isTrailerAShort.value = isShort
            // You'll then observe isTrailerAShort in your Activity
        }
    }

    fun isYoutubeShortByUrlCheck(videoId: String): Boolean {
        try {
            val url = URL("https://www.youtube.com/shorts/$videoId")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "HEAD"
            connection.connectTimeout = 5000 // 5 seconds timeout
            connection.readTimeout = 5000   // 5 seconds timeout
            // Allow redirects is true by default for HttpURLConnection

            val responseCode = connection.responseCode
            connection.disconnect()
            return responseCode == HttpURLConnection.HTTP_OK // 200
        } catch (e: Exception) {
            // Log the error e.g., Log.e("ViewModel", "Error checking short URL", e)
            return false // Handle network errors or other exceptions
        }
    }

    companion object {
        fun isYoutubeShortByUrlCheck(trailerId: String) : Boolean {
            try {
                val url = URL("https://www.youtube.com/shorts/$trailerId")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "HEAD"
                connection.connectTimeout = 5000 // 5 seconds timeout
                connection.readTimeout = 5000   // 5 seconds timeout
                // Allow redirects is true by default for HttpURLConnection

                val responseCode = connection.responseCode
                connection.disconnect()
                return responseCode == HttpURLConnection.HTTP_OK // 200
            } catch (e: Exception) {
                // Log the error e.g., Log.e("ViewModel", "Error checking short URL", e)
                return false // Handle network errors or other exceptions
            }
        }
    }
}