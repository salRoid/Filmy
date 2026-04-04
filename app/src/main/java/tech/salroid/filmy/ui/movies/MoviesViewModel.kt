package tech.salroid.filmy.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val moviePreviewMapper: MoviePreviewMapper
) : ViewModel() {

    val moviesPagingData: Flow<PagingData<MoviePreview>> = moviesRepository.getMovies(
        type = "trending",
        isTrending = true
    ).map { pagingData ->
        pagingData.map(moviePreviewMapper::map)
    }.cachedIn(viewModelScope)
}
