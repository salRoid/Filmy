package tech.salroid.filmy.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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
import tech.salroid.filmy.ui.home.AccountRepository
import tech.salroid.filmy.ui.home.AccountSyncRepository
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.ui.common.model.MediaDetailsUiState
import javax.inject.Inject
import tech.salroid.filmy.utility.reportNonFatal

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val mediaDetailsMapper: MediaDetailsMapper,
    private val accountSyncRepository: AccountSyncRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    companion object {
        private const val WATCHED = "watched"
        private const val WATCHLIST = "watchlist"
    }

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

    private val _userListsLoading = MutableStateFlow(false)
    val userListsLoading: StateFlow<Boolean> = _userListsLoading.asStateFlow()

    /**
     * Lists with an add/remove still on its way to TMDB. Their rows are
     * locked until it finishes: two overlapping requests for one list can
     * land out of order and leave TMDB opposite to what is shown.
     */
    private val _pendingListIds = MutableStateFlow<Set<Int>>(emptySet())
    val pendingListIds: StateFlow<Set<Int>> = _pendingListIds.asStateFlow()

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

    private data class CoreDetails(
        val movie: MovieDetails?,
        val tv: TvDetails?,
        val reviews: ReviewResponse?,
        val watchProviders: WatchProviderResponse?,
        val cast: CastAndCrewResponse?
    )

    private data class RelatedDetails(
        val similar: SimilarMoviesResponse?,
        val recommendations: SimilarMoviesResponse?,
        val ratings: RatingResponse?,
        val releaseDates: ReleaseDatesResponse?,
        val contentRatings: ContentRatingsResponse?
    )

    private data class ExtraDetails(
        val keywords: List<Keyword>,
        val images: ImagesResponse?,
        val externalIds: ExternalIdsResponse?
    )

    private val coreDetails = combine(
        _uiStateMovieDetails,
        _uiStateTvDetails,
        _uiStateReviews,
        _uiStateWatchProviders,
        _uiStateCastAndCrew,
        ::CoreDetails
    )

    private val relatedDetails = combine(
        _uiStateSimilar,
        _uiStateRecommendation,
        _uiStateRatings,
        _uiStateReleaseDates,
        _uiStateContentRatings,
        ::RelatedDetails
    )

    private val extraDetails = combine(
        _uiStateKeywords,
        _uiStateImages,
        _uiStateExternalIds,
        ::ExtraDetails
    )

    /** Whether this title is in at least one of the user's lists, once [loadUserLists] has run. */
    val isInAnyList: StateFlow<Boolean> = _listMembership
        .map { membership -> membership.values.any { it } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val mediaDetailsUiState: StateFlow<MediaDetailsUiState?> = combine(
        coreDetails,
        relatedDetails,
        extraDetails,
        isInAnyList
    ) { core, related, extra, inAnyList ->
        mediaDetailsMapper.map(
            movie = core.movie,
            tv = core.tv,
            reviews = core.reviews,
            watchProviders = core.watchProviders,
            cast = core.cast,
            similar = related.similar,
            recommendations = related.recommendations,
            ratings = related.ratings,
            releaseDates = related.releaseDates,
            contentRatings = related.contentRatings,
            keywords = extra.keywords,
            images = extra.images,
            externalIds = extra.externalIds
        )?.copy(isInList = inAnyList)
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
                        it.reportNonFatal()
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
                        it.reportNonFatal()
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

            val pushed = accountSyncRepository.pushItemState(movieDetails, previous)
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

    /** Synchronous - just a SharedPreferences read, safe to call right before a UI decision. */
    fun isLoggedIn(): Boolean = accountRepository.isLoggedIn()

    /** Synchronous, like [isLoggedIn] - whether the list calls below can run. */
    fun canManageLists(): Boolean = accountRepository.canManageLists()

    /**
     * Fetches the user's TMDB lists and, for each, asks TMDB whether this
     * movie or show is already a member. [userListsLoading] covers the lists
     * themselves; each list's membership then arrives on its own, and is
     * absent from [listMembership] until it does.
     *
     * Runs when the details screen opens (to mark "Add to List" as already
     * listed) and again when the sheet opens; that second run refreshes in
     * place, without a loader or dropping what is already shown.
     */
    fun loadUserLists(mediaId: Int, isTv: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!accountRepository.canManageLists()) return@launch
            val isFirstLoad = _userLists.value.isEmpty()
            _userListsLoading.value = isFirstLoad
            try {
                val lists = accountRepository.getLists().first().results
                _userLists.emit(lists)
                _userListsLoading.value = false
                // Forget lists that have since been deleted.
                val listIds = lists.map { it.id }.toSet()
                _listMembership.update { known -> known.filterKeys { it in listIds } }

                coroutineScope {
                    lists.forEach { list ->
                        launch {
                            val isMember = try {
                                accountRepository.isInList(list.id, mediaId, isTv).first()
                            } catch (e: Exception) {
                                // Unknown is shown as "not in the list" so the row stays usable.
                                e.reportNonFatal()
                                false
                            }
                            _listMembership.update { it + (list.id to isMember) }
                        }
                    }
                }
            } catch (e: Exception) {
                e.reportNonFatal()
            } finally {
                _userListsLoading.value = false
            }
        }
    }

    /**
     * Optimistically flips [listId]'s membership, then pushes the add/remove
     * to TMDB in the background - reverting it if that fails.
     */
    fun toggleListMembership(listId: Int, mediaId: Int, isTv: Boolean, currentlyIn: Boolean) {
        if (!markPending(listId)) return
        setMembership(listId, isMember = !currentlyIn)

        viewModelScope.launch(Dispatchers.IO) {
            val changed = try {
                accountRepository.canManageLists() && if (currentlyIn) {
                    accountRepository.removeFromList(listId, mediaId, isTv).first().allSucceeded
                } else {
                    accountRepository.addToList(listId, mediaId, isTv).first().allSucceeded
                }
            } catch (e: Exception) {
                e.reportNonFatal()
                false
            }
            if (!changed) {
                setMembership(listId, isMember = currentlyIn)
            }
            _pendingListIds.update { it - listId }
        }
    }

    /** False if [listId] already has a request in flight; otherwise marks it and returns true. */
    private fun markPending(listId: Int): Boolean {
        while (true) {
            val current = _pendingListIds.value
            if (listId in current) return false
            if (_pendingListIds.compareAndSet(current, current + listId)) return true
        }
    }

    /**
     * Creates a new list on TMDB and puts this movie or show in it - creating
     * a list from a title's "Add to List" is a request to list that title.
     * If only the add fails, the list is still kept, without the title.
     */
    fun createList(name: String, mediaId: Int, isTv: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!accountRepository.canManageLists()) return@launch
            try {
                val response = accountRepository.createList(name).first()
                val listId = response.listId ?: return@launch
                _pendingListIds.update { it + listId }
                _userLists.value = _userLists.value + TmdbList(id = listId, name = name, itemCount = 0)
                _listMembership.update { it + (listId to false) }

                val added = try {
                    accountRepository.addToList(listId, mediaId, isTv).first().allSucceeded
                } catch (e: Exception) {
                    e.reportNonFatal()
                    false
                }
                if (added) {
                    setMembership(listId, isMember = true)
                }
                _pendingListIds.update { it - listId }
            } catch (e: Exception) {
                e.reportNonFatal()
            }
        }
    }

    /** Records membership and keeps the list's shown item count in step with it. */
    private fun setMembership(listId: Int, isMember: Boolean) {
        val wasMember = _listMembership.value[listId] == true
        _listMembership.update { it + (listId to isMember) }
        if (wasMember != isMember) {
            val delta = if (isMember) 1 else -1
            _userLists.update { lists ->
                lists.map {
                    if (it.id == listId) it.copy(itemCount = ((it.itemCount ?: 0) + delta).coerceAtLeast(0)) else it
                }
            }
        }
    }
}
