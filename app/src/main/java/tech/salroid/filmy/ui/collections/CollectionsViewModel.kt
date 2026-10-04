package tech.salroid.filmy.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.ui.home.AccountSyncRepository
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val accountSyncRepository: AccountSyncRepository
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    val watched: StateFlow<List<MovieDetails>> = moviesRepository.getWatched()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val watchlist: StateFlow<List<MovieDetails>> = moviesRepository.getWatchlist()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /**
     * Called from the screen every time it becomes visible (not just once per
     * ViewModel lifetime) - this ViewModel is preserved across bottom-nav tab
     * switches (saveState/restoreState), so relying on `init` here would mean a
     * pre-login visit "uses up" the one-time trigger and a post-login revisit
     * would never actually sync. [AccountSyncRepository.syncIfNeeded] itself still
     * only does real work once per app process, so calling this repeatedly is safe.
     */
    fun trySync() {
        viewModelScope.launch(Dispatchers.IO) {
            _isSyncing.emit(true)
            accountSyncRepository.syncIfNeeded()
            _isSyncing.emit(false)
        }
    }

    // Legacy support for Fragments
    val uiStateFavorites: StateFlow<List<MovieDetails>> = watched
    val uiStateWatchlist: StateFlow<List<MovieDetails>> = watchlist

    fun getWatchedList() {
        // No-op, now reactive via watched StateFlow
    }

    fun getWatchLists() {
        // No-op, now reactive via watchlist StateFlow
    }

    /**
     * Optimistically removes [movie] from Watched locally, then pushes the change
     * to TMDB. If that push fails (while logged in), the removal is rolled back
     * so local state doesn't drift from the real TMDB account.
     */
    fun removeWatched(movie: MovieDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = movie.copy(watched = false)
            moviesRepository.addMovieDetailsToLocal(updated)
            val pushed = accountSyncRepository.pushItemState(updated)
            if (!pushed) {
                moviesRepository.addMovieDetailsToLocal(movie)
            }
        }
    }

    /** Same as [removeWatched], but for Watchlist. */
    fun removeWatchlist(movie: MovieDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = movie.copy(watchlist = false)
            moviesRepository.addMovieDetailsToLocal(updated)
            val pushed = accountSyncRepository.pushItemState(updated)
            if (!pushed) {
                moviesRepository.addMovieDetailsToLocal(movie)
            }
        }
    }

    fun updateMovieDetailsInDb(
        movie: MovieDetails,
        position: Int,
        currentCollectionType: CollectionTypeFragment.CollectionType
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.updateMovieDetails(movie)
        }
    }
}
