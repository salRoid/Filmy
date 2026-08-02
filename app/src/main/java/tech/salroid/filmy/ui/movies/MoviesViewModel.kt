package tech.salroid.filmy.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val moviePreviewMapper: MoviePreviewMapper
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(Movie.MovieType.TRENDING)
    val selectedCategory: StateFlow<Movie.MovieType> = _selectedCategory

    val moviesPagingData: Flow<PagingData<MoviePreview>> = _selectedCategory
        .flatMapLatest { category ->
            moviesRepository.getMovies(
                type = category.toApiTypeString(),
                isTrending = category == Movie.MovieType.TRENDING
            )
        }
        .map { pagingData -> pagingData.map(moviePreviewMapper::map) }
        .cachedIn(viewModelScope)

    fun onCategorySelected(category: Movie.MovieType) {
        _selectedCategory.value = category
    }

    private fun Movie.MovieType.toApiTypeString(): String = when (this) {
        Movie.MovieType.TRENDING -> "day"
        Movie.MovieType.POPULAR -> "popular"
        Movie.MovieType.NOW_PLAYING -> "now_playing"
        Movie.MovieType.UPCOMING -> "upcoming"
        Movie.MovieType.TOP_RATED -> "top_rated"
    }
}
