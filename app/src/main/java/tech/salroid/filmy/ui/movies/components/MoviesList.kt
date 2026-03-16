package tech.salroid.filmy.ui.movies.components

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.LocalWindowSizeClass
import tech.salroid.filmy.ui.common.components.PreviewList
import tech.salroid.filmy.ui.movies.dummyMoviePreview

@Composable
fun MoviesList(
    modifier: Modifier = Modifier,
    movies: List<MoviePreview>,
    onMovieClick: (Int) -> Unit
) {
    PreviewList(
        modifier = modifier,
        items = movies.size,
        key = { index ->
            movies[index].id
        },
        content = { index ->
            MovieItem(
                movie = movies[index],
                onMovieClick = {
                    onMovieClick(movies[index].id)
                }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MoviesListPreview() {
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo(
        supportLargeAndXLargeWidth = true
    ).windowSizeClass

    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        MoviesList(
            modifier = Modifier,
            movies = (1..10).map { id ->
                dummyMoviePreview.copy(id = id)
            },
            onMovieClick = { }
        )
    }
}