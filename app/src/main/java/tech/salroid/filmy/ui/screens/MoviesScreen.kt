package tech.salroid.filmy.ui.screens

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.ui.components.MovieItem

@Composable
fun MoviesScreen(
    modifier: Modifier,
    viewModel: MoviesViewModel = viewModel()
) {

    val moviesUiState by viewModel.

}


@Composable
fun MoviesList(movies: List<Movie>) {
    LazyVerticalGrid(columns = GridCells.Fixed(3)) {
        items(movies) {
            MovieItem(modifier = Modifier, movie = it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoviesListPreview() {
    MoviesList(
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
    releaseDate = "2026-01-06",

)