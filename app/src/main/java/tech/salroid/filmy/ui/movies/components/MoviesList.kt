package tech.salroid.filmy.ui.movies.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.LocalWindowSizeClass
import tech.salroid.filmy.ui.movies.dummyMoviePreview

@Composable
fun MoviesList(
    modifier: Modifier = Modifier,
    movies: ImmutableList<MoviePreview>,
    onMovieClick: (Int) -> Unit
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val cells = when {
        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
        ) -> 6

        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        ) -> 4

        else -> 3
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(cells),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(movies, key = { it.id }) {
            MovieItem(movie = it, onMovieClick = onMovieClick)
        }
    }
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
            }.toImmutableList(),
            onMovieClick = { }
        )
    }
}