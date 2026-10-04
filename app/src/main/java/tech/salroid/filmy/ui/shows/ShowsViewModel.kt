package tech.salroid.filmy.ui.shows

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.TvShow
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.common.components.QuickActionState
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.utility.ImageConfig
import tech.salroid.filmy.utility.PreferenceHelper.selectedCountryFlow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ShowsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val mapper: TvShowsPreviewMapper,
    sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(TvShow.ShowType.TRENDING)
    val selectedCategory: StateFlow<TvShow.ShowType> = _selectedCategory

    val showsPagingData: Flow<PagingData<TvShowPreview>> = combine(
        _selectedCategory,
        sharedPreferences.selectedCountryFlow()
    ) { category, _ -> category }
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

    /**
     * Quick actions from the long-press menu on a poster - upserts against
     * whatever local record already exists (preserving its other fields)
     * rather than blindly overwriting, so this is safe even if the title was
     * already saved with full details from a previous visit.
     */
    suspend fun getQuickActionState(show: TvShowPreview): QuickActionState = withContext(Dispatchers.IO) {
        val existing = moviesRepository.getMovieDetailsFromLocal(show.id, 1)
        QuickActionState(
            isWatchlisted = existing?.watchlist ?: false,
            isWatched = existing?.watched ?: false
        )
    }

    fun quickToggleWatchlist(show: TvShowPreview) {
        viewModelScope.launch(Dispatchers.IO) {
            val merged = mergedLocalDetails(show)
            moviesRepository.addMovieDetailsToLocal(merged.copy(watchlist = !merged.watchlist))
        }
    }

    fun quickToggleWatched(show: TvShowPreview) {
        viewModelScope.launch(Dispatchers.IO) {
            val merged = mergedLocalDetails(show)
            moviesRepository.addMovieDetailsToLocal(merged.copy(watched = !merged.watched))
        }
    }

    private fun mergedLocalDetails(show: TvShowPreview): MovieDetails {
        val existing = moviesRepository.getMovieDetailsFromLocal(show.id, 1)
        return (existing ?: MovieDetails(id = show.id, type = 1)).copy(
            title = show.title,
            posterPath = show.posterUrl.removePrefix(ImageConfig.BASE_URL)
        )
    }

    private fun TvShow.ShowType.toApiTypeString(): String = when (this) {
        TvShow.ShowType.TRENDING -> "day"
        TvShow.ShowType.POPULAR -> "popular"
        TvShow.ShowType.AIRING_TODAY -> "airing_today"
        TvShow.ShowType.ON_TV -> "on_the_air"
        TvShow.ShowType.TOP_RATED -> "top_rated"
    }
}
