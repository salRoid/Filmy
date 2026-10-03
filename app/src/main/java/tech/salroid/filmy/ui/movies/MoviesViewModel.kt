package tech.salroid.filmy.ui.movies

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
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.common.components.QuickActionState
import tech.salroid.filmy.ui.home.AccountSyncRepository
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.utility.ApiLanguage
import tech.salroid.filmy.utility.ImageConfig
import tech.salroid.filmy.utility.PreferenceHelper.selectedCountryFlow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val moviePreviewMapper: MoviePreviewMapper,
    private val accountSyncRepository: AccountSyncRepository,
    sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(Movie.MovieType.TRENDING)
    val selectedCategory: StateFlow<Movie.MovieType> = _selectedCategory

    val moviesPagingData: Flow<PagingData<MoviePreview>> = combine(
        _selectedCategory,
        sharedPreferences.selectedCountryFlow(),
        ApiLanguage.tag
    ) { category, _, _ -> category }
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

    /**
     * Quick actions from the long-press menu on a poster - upserts against
     * whatever local record already exists (preserving its other fields)
     * rather than blindly overwriting, so this is safe even if the title was
     * already saved with full details from a previous visit.
     */
    suspend fun getQuickActionState(movie: MoviePreview): QuickActionState = withContext(Dispatchers.IO) {
        val existing = moviesRepository.getMovieDetailsFromLocal(movie.id, 0)
        QuickActionState(
            isWatchlisted = existing?.watchlist ?: false,
            isWatched = existing?.watched ?: false
        )
    }

    fun quickToggleWatchlist(movie: MoviePreview) {
        viewModelScope.launch(Dispatchers.IO) {
            val merged = mergedLocalDetails(movie)
            val updated = merged.copy(watchlist = !merged.watchlist)
            moviesRepository.addMovieDetailsToLocal(updated)
            if (!accountSyncRepository.pushItemState(updated)) {
                moviesRepository.addMovieDetailsToLocal(merged)
            }
        }
    }

    fun quickToggleWatched(movie: MoviePreview) {
        viewModelScope.launch(Dispatchers.IO) {
            val merged = mergedLocalDetails(movie)
            val updated = merged.copy(watched = !merged.watched)
            moviesRepository.addMovieDetailsToLocal(updated)
            if (!accountSyncRepository.pushItemState(updated)) {
                moviesRepository.addMovieDetailsToLocal(merged)
            }
        }
    }

    private fun mergedLocalDetails(movie: MoviePreview): MovieDetails {
        val existing = moviesRepository.getMovieDetailsFromLocal(movie.id, 0)
        return (existing ?: MovieDetails(id = movie.id)).copy(
            title = movie.title,
            posterPath = movie.posterUrl.removePrefix(ImageConfig.BASE_URL)
        )
    }

    private fun Movie.MovieType.toApiTypeString(): String = when (this) {
        Movie.MovieType.TRENDING -> "day"
        Movie.MovieType.POPULAR -> "popular"
        Movie.MovieType.NOW_PLAYING -> "now_playing"
        Movie.MovieType.UPCOMING -> "upcoming"
        Movie.MovieType.TOP_RATED -> "top_rated"
    }
}
