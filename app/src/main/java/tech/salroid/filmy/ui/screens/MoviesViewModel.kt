package tech.salroid.filmy.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

sealed class MoviesScreenState {
    object Loading : MoviesScreenState()
    data class Success(val moviesList: List<Movie>) : MoviesScreenState()
    data class Error(val errorMessage: String) : MoviesScreenState()
}

@HiltViewModel
class MoviesViewModel @Inject constructor(
    moviesRepository: MoviesRepository
) : ViewModel() {

    val movieScreenState = moviesRepository.getMoviesFlow(
        type = "trending",
        isTrending = true
    ).catch {
        MoviesScreenState.Error(it.message ?: "Something went wrong.")
    }.map {
        MoviesScreenState.Success(it.results)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MoviesScreenState.Loading
    )
}


