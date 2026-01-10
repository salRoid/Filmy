package tech.salroid.filmy.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.ui.composables.ErrorWidget
import tech.salroid.filmy.ui.composables.LoadingWidget
import tech.salroid.filmy.ui.composables.MovieItem

@Composable
fun MoviesScreen(
    modifier: Modifier,
    viewModel: MoviesViewModel = hiltViewModel()
) {
    val moviesUiState by viewModel.movieScreenState.collectAsStateWithLifecycle()

    when (moviesUiState) {
        is MoviesScreenState.Loading -> LoadingWidget(modifier = Modifier)
        is MoviesScreenState.Success -> MoviesList(
            modifier = modifier,
            movies = (moviesUiState as MoviesScreenState.Success).moviesList
        )

        is MoviesScreenState.Error -> ErrorWidget(
            modifier = Modifier,
            message = (moviesUiState as MoviesScreenState.Error).errorMessage
        )
    }
}

@Composable
fun MoviesList(modifier: Modifier, movies: List<Movie>) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(movies) {
            MovieItem(modifier = Modifier, movie = it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesListPreview() {
    MoviesList(
        modifier = Modifier,
        movies = listOf(
            dummyMovie,
            dummyMovie,
            dummyMovie,
            dummyMovie,
            dummyMovie,
            dummyMovie,
            dummyMovie,
            dummyMovie
        )
    )
}

val dummyMovie = Movie(
    id = 12141,
    title = "People We Meet On Vacation",
    releaseDate = "2026-01-06"
)