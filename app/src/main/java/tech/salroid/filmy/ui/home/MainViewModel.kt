package tech.salroid.filmy.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.model.MoviesUiState
import tech.salroid.filmy.data.local.model.TvShow
import tech.salroid.filmy.data.local.model.TvShowUiState
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiStateMovies: MutableStateFlow<MoviesUiState> =
        MutableStateFlow(MoviesUiState.Loading)
    private val _uiStateTvShows: MutableStateFlow<TvShowUiState> =
        MutableStateFlow(TvShowUiState.Loading)

    private val _uiStateNavigationVisibility = MutableStateFlow(true)
    val uiStateNavigationVisibility = _uiStateNavigationVisibility.asStateFlow()

    private val _uiStateMovieItem = MutableStateFlow(Movie.MovieType.TRENDING)
    private val _uiStateTvItem = MutableStateFlow(TvShow.ShowType.TRENDING)

    val uiStateMovies: StateFlow<MoviesUiState> = _uiStateMovies
    val uiStateTvShows: StateFlow<TvShowUiState> = _uiStateTvShows

    val movies = _uiStateMovieItem.flatMapLatest {
        moviesRepository.getMovies(
            type = it.toMovieTypeString().removePrefix("movie_"),
            isTrending = it == Movie.MovieType.TRENDING
        )
    }.cachedIn(viewModelScope)

    val tvShows = _uiStateTvItem.flatMapLatest {
        moviesRepository.getTvShows(
            type = it.toShowTypeString().removePrefix("tv_show_"),
            isTrending = it == TvShow.ShowType.TRENDING
        )
    }.cachedIn(viewModelScope)

    fun onMovieItemSelected(item: Movie.MovieType) {
        viewModelScope.launch {
            _uiStateMovieItem.emit(item)
        }
    }

    fun onTvItemSelected(item: TvShow.ShowType) {
        viewModelScope.launch {
            _uiStateTvItem.emit(item)
        }
    }

    fun getCurrentSelectedMovieItem() = _uiStateMovieItem.value

    fun getCurrentSelectedTvItem() = _uiStateTvItem.value
}