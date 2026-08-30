package tech.salroid.filmy.ui.movies.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.common.components.PaginatedPreviewList
import tech.salroid.filmy.ui.common.components.QuickActionState

@Composable
fun MoviesList(
    modifier: Modifier = Modifier,
    movies: LazyPagingItems<MoviePreview>,
    onMovieClick: (Int) -> Unit,
    fetchQuickActionState: suspend (MoviePreview) -> QuickActionState = { QuickActionState(false, false) },
    onQuickToggleWatchlist: (MoviePreview) -> Unit = {},
    onQuickToggleWatched: (MoviePreview) -> Unit = {}
) {
    PaginatedPreviewList(
        modifier = modifier,
        items = movies,
        content = { movie ->
            MovieItem(
                movie = movie,
                onMovieClick = {
                    onMovieClick(movie.id)
                },
                fetchQuickActionState = { fetchQuickActionState(movie) },
                onToggleWatchlist = { onQuickToggleWatchlist(movie) },
                onToggleWatched = { onQuickToggleWatched(movie) }
            )
        }
    )
}
