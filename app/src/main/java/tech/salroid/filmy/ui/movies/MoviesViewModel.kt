package tech.salroid.filmy.ui.movies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    moviesRepository: MoviesRepository,
    moviePreviewMapper: MoviePreviewMapper
) : ViewModel() {

    val movieScreenState = moviesRepository.getMoviesFlow(
        type = "trending",
        isTrending = true
    ).map { response ->

        val previews = response.results
            .map(moviePreviewMapper::map)
            .toImmutableList()

        MoviesScreenState.Success(previews)
    }.catch {
        MoviesScreenState.Error(it.message ?: "Something went wrong.")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MoviesScreenState.Loading
    )
}

