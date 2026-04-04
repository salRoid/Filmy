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
import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.ui.details.MovieDetailsActivity.Companion.WATCHED
import tech.salroid.filmy.ui.details.MovieDetailsActivity.Companion.WATCHLIST
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.ui.common.model.MediaDetailsUiState
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val mediaDetailsMapper: MediaDetailsMapper
) : ViewModel() {

    private val _uiStateMovieDetails = MutableStateFlow<MovieDetails?>(null)
    private val _uiStateTvDetails = MutableStateFlow<TvDetails?>(null)
    private val _uiStateRatings = MutableStateFlow<RatingResponse?>(null)
    private val _uiStateReviews = MutableStateFlow<ReviewResponse?>(null)
    private val _uiStateWatchProviders = MutableStateFlow<WatchProviderResponse?>(null)
    
    private val _uiStateCastAndCrew = MutableStateFlow<CastAndCrewResponse?>(null)
    private val _uiStateSimilar = MutableStateFlow<SimilarMoviesResponse?>(null)
    private val _uiStateRecommendation = MutableStateFlow<SimilarMoviesResponse?>(null)

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

    val mediaDetailsUiState: StateFlow<MediaDetailsUiState?> = combine(
        _uiStateMovieDetails,
        _uiStateTvDetails,
        _uiStateReviews,
        _uiStateWatchProviders,
        _uiStateCastAndCrew,
        _uiStateSimilar,
        _uiStateRecommendation,
        _uiStateRatings
    ) { args ->
        val movie = args[0] as MovieDetails?
        val tv = args[1] as TvDetails?
        val reviews = args[2] as ReviewResponse?
        val watchProviders = args[3] as WatchProviderResponse?
        val cast = args[4] as CastAndCrewResponse?
        val similar = args[5] as SimilarMoviesResponse?
        val recommendations = args[6] as SimilarMoviesResponse?
        val ratings = args[7] as RatingResponse?

        mediaDetailsMapper.map(
            movie = movie,
            tv = tv,
            reviews = reviews,
            watchProviders = watchProviders,
            cast = cast,
            similar = similar,
            recommendations = recommendations,
            ratings = ratings
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun fetchAllMovieDetails(movieId: String, movieType: Int, addToLocal: Boolean = false) {
        getMovieDetails(movieId, movieType, addToLocal)
        getReviews(movieId)
        getWatchProviders(movieId)
        
        viewModelScope.launch {
            moviesRepository.getCastAndCrew(movieId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateCastAndCrew.emit(it) }
        }
        viewModelScope.launch {
            moviesRepository.getSimilar(movieId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateSimilar.emit(it) }
        }
        viewModelScope.launch {
            moviesRepository.getRecommendation(movieId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateRecommendation.emit(it) }
        }
    }

    fun fetchAllTvDetails(showId: String, movieType: Int, addToLocal: Boolean = false) {
        getTvDetails(showId, movieType, addToLocal)
        getTvReviews(showId)
        getWatchProvidersTv(showId)

        viewModelScope.launch {
            moviesRepository.getCastAndCrewTv(showId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateCastAndCrew.emit(it) }
        }
        viewModelScope.launch {
            moviesRepository.getSimilarTv(showId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateSimilar.emit(it) }
        }
        viewModelScope.launch {
            moviesRepository.getRecommendationTv(showId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateRecommendation.emit(it) }
        }
    }

    fun getMovieDetails(movieId: String?, movieType: Int, addToLocal: Boolean = false) {
        movieId?.toInt()?.let { id ->
            viewModelScope.launch(Dispatchers.IO) {
                moviesRepository.getMovieDetailsFlow(id, movieType)
                    .collect { details ->
                        _uiStateMovieDetails.emit(details)
                        if (details?.imdbId != null) {
                            getRatings(details.imdbId)
                        }
                    }
            }

            viewModelScope.launch(Dispatchers.IO) {
                moviesRepository.getMovieDetailsFromNetwork(movieId)
                    .catch { it.printStackTrace() }
                    .collect { details ->
                        val local = moviesRepository.getMovieDetailsFromLocal(id, movieType)
                        val updated = details.copy(
                            watched = local?.watched ?: false,
                            watchlist = local?.watchlist ?: false,
                            type = movieType
                        )
                        if (addToLocal || local != null) {
                            moviesRepository.addMovieDetailsToLocal(updated)
                        } else {
                            _uiStateMovieDetails.emit(updated)
                        }
                        
                        if (updated.imdbId != null) {
                            getRatings(updated.imdbId)
                        }
                    }
            }
        }
    }

    fun getTvDetails(showId: String?, movieType: Int, addToLocal: Boolean = false) {
        showId?.toInt()?.let { id ->
            viewModelScope.launch(Dispatchers.IO) {
                moviesRepository.getMovieDetailsFlow(id, movieType)
                    .collect { details ->
                        _uiStateMovieDetails.emit(details)
                    }
            }

            viewModelScope.launch(Dispatchers.IO) {
                moviesRepository.getTvShowDetailsFromNetwork(showId)
                    .catch { it.printStackTrace() }
                    .collect { details ->
                        _uiStateTvDetails.emit(details)
                    }
            }
        }
    }

    fun saveMovieDetailsInDb(
        details: MovieDetails,
        type: Int = 0,
        isWatchlist: Boolean = false,
        isWatched: Boolean = false,
        addedToCollection: Boolean = false,
        message: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val movieDetails = details.copy(
                watchlist = isWatchlist,
                watched = isWatched,
                type = type
            )

            if (addedToCollection) {
                if (message == WATCHLIST) movieDetails.watchlist = true
                if (message == WATCHED) movieDetails.watched = true
            }

            moviesRepository.addMovieDetailsToLocal(movieDetails)
            if (addedToCollection) {
                _uiStateAddToCollection.emit(Pair(true, message))
            }
        }
    }

    fun toggleWatched(movieDetails: MovieDetails) {
        val updatedMovie = movieDetails.copy(watched = !movieDetails.watched)
        updateMovieDetailsInDb(updatedMovie, WATCHED, !updatedMovie.watched)
    }

    fun toggleWatchlist(movieDetails: MovieDetails) {
        val updatedMovie = movieDetails.copy(watchlist = !movieDetails.watchlist)
        updateMovieDetailsInDb(updatedMovie, WATCHLIST, !updatedMovie.watchlist)
    }

    fun toggleWatchedTv(showDetails: TvDetails, currentMovieDetails: MovieDetails?) {
        val movieDetails = currentMovieDetails ?: MovieDetails(
            id = showDetails.id ?: 0,
            title = showDetails.name,
            overview = showDetails.overview,
            tagline = showDetails.tagline,
            backdropPath = showDetails.backdropPath,
            posterPath = showDetails.posterPath,
            voteAverage = showDetails.voteAverage,
            voteCount = showDetails.voteCount?.toLong(),
            originalLanguage = showDetails.originalLanguage,
            type = 1
        )
        val updatedMovie = movieDetails.copy(watched = !movieDetails.watched)
        updateMovieDetailsInDb(updatedMovie, WATCHED, !updatedMovie.watched)
    }

    fun toggleWatchlistTv(showDetails: TvDetails, currentMovieDetails: MovieDetails?) {
        val movieDetails = currentMovieDetails ?: MovieDetails(
            id = showDetails.id ?: 0,
            title = showDetails.name,
            overview = showDetails.overview,
            tagline = showDetails.tagline,
            backdropPath = showDetails.backdropPath,
            posterPath = showDetails.posterPath,
            voteAverage = showDetails.voteAverage,
            voteCount = showDetails.voteCount?.toLong(),
            originalLanguage = showDetails.originalLanguage,
            type = 1
        )
        val updatedMovie = movieDetails.copy(watchlist = !movieDetails.watchlist)
        updateMovieDetailsInDb(updatedMovie, WATCHLIST, !updatedMovie.watchlist)
    }

    fun updateMovieDetailsInDb(
        movieDetails: MovieDetails,
        message: String,
        remove: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.addMovieDetailsToLocal(movieDetails)
            _uiStateUpdateCollection.emit(Triple(movieDetails.id, message, remove))
        }
    }

    fun getRatings(ratingID: String?) {
        viewModelScope.launch {
            ratingID?.let {
                moviesRepository.getRatings(it)
                    .flowOn(Dispatchers.IO)
                    .catch { }
                    .collect { ratingResponse ->
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
                    .catch { }
                    .collect { reviewResponse ->
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
                    .catch { }
                    .collect { reviewResponse ->
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
                    .catch { }
                    .collect { watchProvidersResponse ->
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
                    .catch { }
                    .collect { watchProvidersResponse ->
                        _uiStateWatchProviders.emit(watchProvidersResponse)
                    }
            }
        }
    }
}