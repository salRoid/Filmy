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

    private val _uiStateFavorite = MutableStateFlow<List<MovieDetails>?>(null)
    private val _uiStateWatchlist = MutableStateFlow<List<MovieDetails>?>(null)
    private val _uiStateRemoved = MutableStateFlow<Int?>(null)

    val uiStateFavorites: StateFlow<List<MovieDetails>?> = _uiStateFavorite.asStateFlow()
    val uiStateWatchlist: StateFlow<List<MovieDetails>?> = _uiStateWatchlist.asStateFlow()
    val uiStateRemoved: StateFlow<Int?> = _uiStateRemoved.asStateFlow()

    fun getFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val movies = moviesRepository.getFavorites()
            if (movies.isNotEmpty()) {
                _uiStateFavorite.emit(movies)
            }
        }
    }

    fun getWatchLists() {
        viewModelScope.launch(Dispatchers.IO) {
            val movies = moviesRepository.getWatchlist()
            if (movies.isNotEmpty()) {
                _uiStateWatchlist.emit(movies)
            }
        }
    }

    fun updateMovieDetailsInDb(movie: MovieDetails, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            moviesRepository.updateMovieDetails(movie)
            _uiStateRemoved.emit(position)
        }
    }
}