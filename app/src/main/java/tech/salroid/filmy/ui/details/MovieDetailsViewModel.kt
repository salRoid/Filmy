package tech.salroid.filmy.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.ContentRatingsResponse
import tech.salroid.filmy.data.local.model.ExternalIdsResponse
import tech.salroid.filmy.data.local.model.ImagesResponse
import tech.salroid.filmy.data.local.model.Keyword
import tech.salroid.filmy.data.local.model.RatingResponse
import tech.salroid.filmy.data.local.model.ReleaseDatesResponse
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.data.local.model.account.TmdbList
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.ui.details.MovieDetailsActivity.Companion.WATCHED
import tech.salroid.filmy.ui.details.MovieDetailsActivity.Companion.WATCHLIST
import tech.salroid.filmy.ui.home.AccountRepository
import tech.salroid.filmy.ui.home.AccountSyncRepository
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.ui.common.model.MediaDetailsUiState
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val mediaDetailsMapper: MediaDetailsMapper,
    private val accountSyncRepository: AccountSyncRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _uiStateMovieDetails = MutableStateFlow<MovieDetails?>(null)
    private val _uiStateTvDetails = MutableStateFlow<TvDetails?>(null)
    private val _uiStateRatings = MutableStateFlow<RatingResponse?>(null)
    private val _uiStateReviews = MutableStateFlow<ReviewResponse?>(null)
    private val _uiStateWatchProviders = MutableStateFlow<WatchProviderResponse?>(null)

    private val _uiStateCastAndCrew = MutableStateFlow<CastAndCrewResponse?>(null)
    private val _uiStateSimilar = MutableStateFlow<SimilarMoviesResponse?>(null)
    private val _uiStateRecommendation = MutableStateFlow<SimilarMoviesResponse?>(null)
    private val _uiStateReleaseDates = MutableStateFlow<ReleaseDatesResponse?>(null)
    private val _uiStateContentRatings = MutableStateFlow<ContentRatingsResponse?>(null)
    private val _uiStateKeywords = MutableStateFlow<List<Keyword>>(emptyList())
    private val _uiStateImages = MutableStateFlow<ImagesResponse?>(null)
    private val _uiStateExternalIds = MutableStateFlow<ExternalIdsResponse?>(null)

    private val _uiStateAddToCollection = MutableStateFlow<Pair<Boolean, String?>>(Pair(false, ""))
    private val _uiStateUpdateCollection = MutableStateFlow(Triple(-1, "", false))

    /** True when the core details fetch failed and there's no local copy to fall back on. */
    private val _uiStateError = MutableStateFlow(false)
    val uiStateError: StateFlow<Boolean> = _uiStateError.asStateFlow()

    private val _userLists = MutableStateFlow<List<TmdbList>>(emptyList())
    val userLists: StateFlow<List<TmdbList>> = _userLists.asStateFlow()

    /** listId -> whether the currently-open movie is a member of that list. */
    private val _listMembership = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val listMembership: StateFlow<Map<Int, Boolean>> = _listMembership.asStateFlow()

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
        _uiStateRatings,
        _uiStateReleaseDates,
        _uiStateContentRatings,
        _uiStateKeywords,
        _uiStateImages,
        _uiStateExternalIds
    ) { args ->
        val movie = args[0] as MovieDetails?
        val tv = args[1] as TvDetails?
        val reviews = args[2] as ReviewResponse?
        val watchProviders = args[3] as WatchProviderResponse?
        val cast = args[4] as CastAndCrewResponse?
        val similar = args[5] as SimilarMoviesResponse?
        val recommendations = args[6] as SimilarMoviesResponse?
        val ratings = args[7] as RatingResponse?
        val releaseDates = args[8] as ReleaseDatesResponse?
        val contentRatings = args[9] as ContentRatingsResponse?
        @Suppress("UNCHECKED_CAST")
        val keywords = args[10] as List<Keyword>
        val images = args[11] as ImagesResponse?
        val externalIds = args[12] as ExternalIdsResponse?

        mediaDetailsMapper.map(
            movie = movie,
            tv = tv,
            reviews = reviews,
            watchProviders = watchProviders,
            cast = cast,
            similar = similar,
            recommendations = recommendations,
            ratings = ratings,
            releaseDates = releaseDates,
            contentRatings = contentRatings,
            keywords = keywords,
            images = images,
            externalIds = externalIds
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun fetchAllMovieDetails(movieId: String, movieType: Int, addToLocal: Boolean = false) {
        _uiStateError.value = false
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
        viewModelScope.launch {
            moviesRepository.getMovieCertification(movieId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateReleaseDates.emit(it) }
        }
        viewModelScope.launch {
            moviesRepository.getMovieKeywords(movieId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateKeywords.emit(it) }
        }
        viewModelScope.launch {
            moviesRepository.getMovieImages(movieId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateImages.emit(it) }
        }
        viewModelScope.launch {
            moviesRepository.getMovieExternalIds(movieId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateExternalIds.emit(it) }
        }
    }

    fun fetchAllTvDetails(showId: String, movieType: Int, addToLocal: Boolean = false) {
        _uiStateError.value = false
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
        viewModelScope.launch {
            moviesRepository.getTvCertification(showId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateContentRatings.emit(it) }
        }
        viewModelScope.launch {
            moviesRepository.getTvExternalIds(showId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { externalIds ->
                    _uiStateExternalIds.emit(externalIds)
                    externalIds.imdbId?.let { getRatings(it) }
                }
        }
        viewModelScope.launch {
            moviesRepository.getTvKeywords(showId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateKeywords.emit(it) }
        }
        viewModelScope.launch {
            moviesRepository.getTvImages(showId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _uiStateImages.emit(it) }
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
                    .catch {
                        it.printStackTrace()
                        if (moviesRepository.getMovieDetailsFromLocal(id, movieType) == null) {
                            _uiStateError.emit(true)
                        }
                    }
                    .collect { details ->
                        val local = moviesRepository.getMovieDetailsFromLocal(id, movieType)
                        val updated = details.copy(
                            watched = local?.watched ?: false,
                            watchlist = local?.watchlist ?: false,
                            userRating = local?.userRating,
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
                    .catch {
                        it.printStackTrace()
                        if (moviesRepository.getMovieDetailsFromLocal(id, movieType) == null) {
                            _uiStateError.emit(true)
                        }
                    }
                    .collect { showDetails ->
                        _uiStateTvDetails.emit(showDetails)
                        
                        // Also update local DB if it's already in the collection
                        val local = moviesRepository.getMovieDetailsFromLocal(id, movieType)
                        if (local != null) {
                            val updated = local.copy(
                                title = showDetails.name,
                                overview = showDetails.overview,
                                tagline = showDetails.tagline,
                                backdropPath = showDetails.backdropPath,
                                posterPath = showDetails.posterPath,
                                voteAverage = showDetails.voteAverage,
                                voteCount = showDetails.voteCount?.toLong(),
                                originalLanguage = showDetails.originalLanguage,
                                releaseDate = showDetails.firstAirDate
                            )
                            moviesRepository.addMovieDetailsToLocal(updated)
                        }
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
        updateMovieDetailsInDb(updatedMovie, WATCHED, !updatedMovie.watched, previous = movieDetails)
    }

    fun toggleWatchlist(movieDetails: MovieDetails) {
        val updatedMovie = movieDetails.copy(watchlist = !movieDetails.watchlist)
        updateMovieDetailsInDb(updatedMovie, WATCHLIST, !updatedMovie.watchlist, previous = movieDetails)
    }

    fun toggleWatchedTv(showDetails: TvDetails, currentMovieDetails: MovieDetails?) {
        val movieDetails = showDetails.toMovieDetails(
            existing = currentMovieDetails,
            watched = !(currentMovieDetails?.watched ?: false),
            watchlist = currentMovieDetails?.watchlist ?: false
        )
        updateMovieDetailsInDb(movieDetails, WATCHED, !movieDetails.watched, previous = currentMovieDetails)
    }

    fun toggleWatchlistTv(showDetails: TvDetails, currentMovieDetails: MovieDetails?) {
        val movieDetails = showDetails.toMovieDetails(
            existing = currentMovieDetails,
            watched = currentMovieDetails?.watched ?: false,
            watchlist = !(currentMovieDetails?.watchlist ?: false)
        )
        updateMovieDetailsInDb(movieDetails, WATCHLIST, !movieDetails.watchlist, previous = currentMovieDetails)
    }

    /**
     * Optimistically writes [movieDetails]'s new [rating] locally, then pushes it to
     * TMDB in the background, rolling back to the previous local row on failure.
     * Pass `null` to remove an existing rating.
     */
    fun rateMovie(movieDetails: MovieDetails, rating: Float?) {
        val previous = movieDetails
        val updated = movieDetails.copy(userRating = rating)
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.addMovieDetailsToLocal(updated)
            val pushed = accountSyncRepository.pushRating(updated)
            if (!pushed) {
                moviesRepository.addMovieDetailsToLocal(previous)
            }
        }
    }

    fun rateTvShow(showDetails: TvDetails, currentMovieDetails: MovieDetails?, rating: Float?) {
        val movieDetails = showDetails.toMovieDetails(
            existing = currentMovieDetails,
            watched = currentMovieDetails?.watched ?: false,
            watchlist = currentMovieDetails?.watchlist ?: false
        )
        rateMovie(movieDetails, rating)
    }

    /**
     * Optimistically writes [movieDetails] locally and reports it via
     * [uiStateUpdateCollection] immediately, then pushes it to TMDB in the
     * background. If that push fails (while logged in), the local row is rolled
     * back to [previous] (or deleted entirely if there was no previous row) and a
     * second collection-update event is emitted reflecting the reverted state.
     */
    fun updateMovieDetailsInDb(
        movieDetails: MovieDetails,
        message: String,
        remove: Boolean,
        previous: MovieDetails? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.addMovieDetailsToLocal(movieDetails)
            _uiStateUpdateCollection.emit(Triple(movieDetails.id, message, remove))

            val pushed = accountSyncRepository.pushItemState(movieDetails)
            if (!pushed) {
                if (previous != null) {
                    moviesRepository.addMovieDetailsToLocal(previous)
                } else {
                    moviesRepository.deleteMovieDetailsFromLocal(movieDetails)
                }
                _uiStateUpdateCollection.emit(Triple(movieDetails.id, message, !remove))
            }
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

    /**
     * Fetches the user's TMDB lists and, for each, checks whether [movieId] is
     * already a member (TMDB has no cheaper "is this movie in this list" lookup,
     * so this fetches each list's items once - fine for a personal number of lists).
     */
    fun loadUserLists(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = accountRepository.getSessionIdFromPref() ?: return@launch
            val accountId = accountRepository.getProfileFromLocal()?.id ?: return@launch
            try {
                val lists = accountRepository.getLists(accountId, sessionId).first().results
                _userLists.emit(lists)

                val membership = mutableMapOf<Int, Boolean>()
                lists.forEach { list ->
                    try {
                        val details = accountRepository.getListDetails(list.id, sessionId).first()
                        membership[list.id] = details.items.any { it.id == movieId }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                _listMembership.emit(membership)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Optimistically flips [listId]'s membership checkbox, then pushes the
     * add/remove to TMDB in the background - reverting the checkbox if it fails.
     */
    fun toggleListMembership(listId: Int, movieId: Int, currentlyIn: Boolean) {
        _listMembership.value = _listMembership.value + (listId to !currentlyIn)

        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = accountRepository.getSessionIdFromPref()
            if (sessionId == null) {
                _listMembership.value = _listMembership.value + (listId to currentlyIn)
                return@launch
            }
            try {
                if (currentlyIn) {
                    accountRepository.removeFromList(listId, sessionId, movieId).first()
                } else {
                    accountRepository.addToList(listId, sessionId, movieId).first()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _listMembership.value = _listMembership.value + (listId to currentlyIn)
            }
        }
    }

    /** Creates a new list on TMDB and appends it to [userLists] on success. */
    fun createList(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = accountRepository.getSessionIdFromPref() ?: return@launch
            try {
                val response = accountRepository.createList(sessionId, name).first()
                val listId = response.listId ?: return@launch
                _userLists.value = _userLists.value + TmdbList(id = listId, name = name, itemCount = 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}