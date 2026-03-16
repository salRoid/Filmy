package tech.salroid.filmy.ui.collections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    val favorites: StateFlow<List<MovieDetails>> = moviesRepository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    val watchlist: StateFlow<List<MovieDetails>> = moviesRepository.getWatchlist()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    // Legacy support for Fragments
    val uiStateFavorites: StateFlow<List<MovieDetails>> = favorites
    val uiStateWatchlist: StateFlow<List<MovieDetails>> = watchlist

    fun getFavorites() {
        // No-op, now reactive via favorites StateFlow
    }

    fun getWatchLists() {
        // No-op, now reactive via watchlist StateFlow
    }

    fun removeFavorite(movie: MovieDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.updateMovieDetails(movie.copy(favorite = false))
        }
    }

    fun removeWatchlist(movie: MovieDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.updateMovieDetails(movie.copy(watchlist = false))
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
