package tech.salroid.filmy.data.local.model

import tech.salroid.filmy.data.local.db.entity.Movie

sealed class MoviesUiState {
    object Loading : MoviesUiState()

    data class Success(
        val movieResponse: MoviesResponse,
        val movieType: Movie.MovieType
    ) : MoviesUiState()

    data class Error(
        val exception: Throwable,
        val movieType: Movie.MovieType
    ) : MoviesUiState()
}