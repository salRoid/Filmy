package tech.salroid.filmy.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.RatingResponse
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.ui.details.MovieDetailsActivity.Companion.FAVOURITES
import tech.salroid.filmy.ui.details.MovieDetailsActivity.Companion.WATCHLIST
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiStateMovieDetails = MutableStateFlow<MovieDetails?>(null)
    private val _uiStateTvDetails = MutableStateFlow<TvDetails?>(null)
    private val _uiStateRatings = MutableStateFlow<RatingResponse?>(null)
    private val _uiStateReviews = MutableStateFlow<ReviewResponse?>(null)
    private val _uiStateWatchProviders = MutableStateFlow<WatchProviderResponse?>(null)
    private val _uiStateAddToCollection = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, ""))
    private val _uiStateUpdateCollection = MutableStateFlow(Triple(-1, "", false))

    val uiStateMovieDetails: StateFlow<MovieDetails?> = _uiStateMovieDetails.asStateFlow()
    val uiStateTvDetails: StateFlow<TvDetails?> = _uiStateTvDetails.asStateFlow()
    val uiStateRatingResponse: StateFlow<RatingResponse?> = _uiStateRatings.asStateFlow()
    val uiStateReviewResponse: StateFlow<ReviewResponse?> = _uiStateReviews.asStateFlow()
    val uiStateWatchProvidersResponse: StateFlow<WatchProviderResponse?> =
        _uiStateWatchProviders.asStateFlow()
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

    fun getTvDetails(showId: String?, movieType: Int, addToLocal: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            showId?.toInt()?.let {
                // val movieDetails = moviesRepository.getMovieDetailsFromLocal(it, movieType)
                // val isWatchList = movieDetails?.watchlist ?: false
                // val isFavourite = movieDetails?.favorite ?: false
                //_uiStateMovieDetails.emit(movieDetails)

                showId.let {
                    moviesRepository.getTvShowDetailsFromNetwork(it)
                        .flowOn(Dispatchers.IO)
                        .catch { throwable ->
                            throwable.printStackTrace()
                        }.collect { details ->
                            val updatedDetails = details.copy()
                            // updatedDetails.watchlist = isWatchList
                            // updatedDetails.favorite = isFavourite
                            _uiStateTvDetails.emit(updatedDetails)

                           // if (addToLocal) {
                                // saveMovieDetailsInDb(updatedDetails)
                           // }
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

    fun getReviews(movieId: String?) {
        viewModelScope.launch {
            movieId?.let {
                moviesRepository.getReviews(it)
                    .flowOn(Dispatchers.IO)
                    .catch {
                        // error
                    }.collect { reviewResponse ->
                        _uiStateReviews.emit(reviewResponse)
                    }
            }
        }
    }

    fun getTvReviews(movieId: String?) {
        viewModelScope.launch {
            movieId?.let {
                moviesRepository.getTvReviews(it)
                    .flowOn(Dispatchers.IO)
                    .catch {
                        // error
                    }.collect { reviewResponse ->
                        _uiStateReviews.emit(reviewResponse)
                    }
            }
        }
    }

    fun getWatchProviders(movieId: String?) {
        viewModelScope.launch {
            movieId?.let {
                moviesRepository.getWatchProviders(it)
                    .flowOn(Dispatchers.IO)
                    .catch {
                        // error
                    }.collect { watchProvidersResponse ->
                        _uiStateWatchProviders.emit(watchProvidersResponse)
                    }
            }
        }
    }

    fun getWatchProvidersTv(tvId: String?) {
        viewModelScope.launch {
            tvId?.let {
                moviesRepository.getWatchProvidersTv(it)
                    .flowOn(Dispatchers.IO)
                    .catch {
                        // error
                    }.collect { watchProvidersResponse ->
                        _uiStateWatchProviders.emit(watchProvidersResponse)
                    }
            }
        }
    }
}