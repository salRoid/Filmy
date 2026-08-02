package tech.salroid.filmy.ui.shows

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
import tech.salroid.filmy.data.local.model.TvShow
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ShowsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val mapper: TvShowsPreviewMapper
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(TvShow.ShowType.TRENDING)
    val selectedCategory: StateFlow<TvShow.ShowType> = _selectedCategory

    val showsPagingData: Flow<PagingData<TvShowPreview>> = _selectedCategory
        .flatMapLatest { category ->
            moviesRepository.getTvShows(
                type = category.toApiTypeString(),
                isTrending = category == TvShow.ShowType.TRENDING
            )
        }
        .map { pagingData -> pagingData.map(mapper::map) }
        .cachedIn(viewModelScope)

    fun onCategorySelected(category: TvShow.ShowType) {
        _selectedCategory.value = category
    }

    private fun TvShow.ShowType.toApiTypeString(): String = when (this) {
        TvShow.ShowType.TRENDING -> "day"
        TvShow.ShowType.POPULAR -> "popular"
        TvShow.ShowType.AIRING_TODAY -> "airing_today"
        TvShow.ShowType.ON_TV -> "on_the_air"
        TvShow.ShowType.TOP_RATED -> "top_rated"
    }
}
