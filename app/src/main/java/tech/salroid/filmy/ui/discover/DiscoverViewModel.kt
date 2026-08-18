package tech.salroid.filmy.ui.discover

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.model.Genre
import tech.salroid.filmy.data.local.model.discover.DiscoverFilters
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.ui.movies.MoviePreviewMapper
import tech.salroid.filmy.ui.shows.TvShowsPreviewMapper
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val moviePreviewMapper: MoviePreviewMapper,
    private val tvShowsPreviewMapper: TvShowsPreviewMapper
) : ViewModel() {

    private var initialized = false
    var isTv: Boolean = false
        private set

    private val _filters = MutableStateFlow(DiscoverFilters())
    val filters: StateFlow<DiscoverFilters> = _filters

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres

    val moviesResults: Flow<PagingData<MoviePreview>> = _filters
        .flatMapLatest { filters -> moviesRepository.discoverMovies(filters) }
        .map { pagingData -> pagingData.map(moviePreviewMapper::map) }
        .cachedIn(viewModelScope)

    val showsResults: Flow<PagingData<TvShowPreview>> = _filters
        .flatMapLatest { filters -> moviesRepository.discoverTv(filters) }
        .map { pagingData -> pagingData.map(tvShowsPreviewMapper::map) }
        .cachedIn(viewModelScope)

    fun initialize(isTv: Boolean, keywordId: Int? = null, keywordName: String? = null) {
        if (initialized) return
        initialized = true
        this.isTv = isTv

        if (keywordId != null && keywordName != null) {
            _filters.value = DiscoverFilters(keywordId = keywordId, keywordName = keywordName)
        }

        viewModelScope.launch {
            try {
                val response = if (isTv) {
                    moviesRepository.getTvGenres().first()
                } else {
                    moviesRepository.getMovieGenres().first()
                }
                _genres.value = response.genres
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun applyFilters(newFilters: DiscoverFilters) {
        _filters.value = newFilters
    }
}
