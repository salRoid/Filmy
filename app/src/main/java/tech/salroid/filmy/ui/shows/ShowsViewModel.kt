package tech.salroid.filmy.ui.shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class ShowsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val mapper: TvShowsPreviewMapper
) : ViewModel() {

    val showsPagingData: Flow<PagingData<TvShowPreview>> = moviesRepository.getTvShows(
        type = "trending",
        isTrending = true
    ).map { pagingData ->
        pagingData.map(mapper::map)
    }.cachedIn(viewModelScope)
}
