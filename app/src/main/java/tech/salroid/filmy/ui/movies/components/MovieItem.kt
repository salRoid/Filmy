package tech.salroid.filmy.ui.movies.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.common.components.PreviewItem
import tech.salroid.filmy.ui.common.components.QuickActionState
import tech.salroid.filmy.ui.movies.dummyMoviePreview

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movie: MoviePreview,
    onMovieClick: () -> Unit,
    fetchQuickActionState: (suspend () -> QuickActionState)? = null,
    onToggleWatchlist: (() -> Unit)? = null,
    onToggleWatched: (() -> Unit)? = null
) {
    PreviewItem(
        modifier = modifier,
        title = movie.title,
        posterUrl = movie.posterUrl,
        readableDate = movie.readableReleaseDate,
        contentDescription = stringResource(R.string.movie_item_content_description, movie.title),
        onItemClick = onMovieClick,
        fetchQuickActionState = fetchQuickActionState,
        onToggleWatchlist = onToggleWatchlist,
        onToggleWatched = onToggleWatched
    )
}

@Preview(
    name = "MovieItem - Preview",
    showBackground = true
)
@Composable
fun MovieItemPreview() {
    MovieItem(
        modifier = Modifier
            .width(140.dp)
            .padding(16.dp),
        movie = dummyMoviePreview,
        onMovieClick = { }
    )
}
