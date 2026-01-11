package tech.salroid.filmy.ui.movies

import kotlinx.collections.immutable.ImmutableList
import tech.salroid.filmy.data.model.MoviePreview

sealed class MoviesScreenState {
    object Loading : MoviesScreenState()
    data class Success(val moviesList: ImmutableList<MoviePreview>) : MoviesScreenState()
    data class Error(val errorMessage: String) : MoviesScreenState()
}