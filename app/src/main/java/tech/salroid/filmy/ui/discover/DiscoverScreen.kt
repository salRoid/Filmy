package tech.salroid.filmy.ui.discover

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.Genre
import tech.salroid.filmy.data.local.model.discover.DiscoverFilters
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.common.components.PaginatedPreviewList
import tech.salroid.filmy.ui.discover.components.DiscoverFilterSheet
import tech.salroid.filmy.ui.movies.components.MovieItem
import tech.salroid.filmy.ui.shows.components.ShowItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    isTv: Boolean,
    movieItems: LazyPagingItems<MoviePreview>?,
    showItems: LazyPagingItems<TvShowPreview>?,
    filters: DiscoverFilters,
    genres: List<Genre>,
    onApplyFilters: (DiscoverFilters) -> Unit,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onShowClick: (Int) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            if (isTv) R.string.discover_shows_title else R.string.discover_movies_title
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(Icons.Default.Tune, contentDescription = stringResource(R.string.discover_filters))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (isTv) {
                val shows = showItems ?: return@Box
                when (val state = shows.loadState.refresh) {
                    is LoadState.Loading -> {
                        LoadingWidget(modifier = Modifier.fillMaxSize())
                    }
                    is LoadState.Error -> {
                        ErrorWidget(
                            modifier = Modifier.fillMaxSize(),
                            message = state.error.message ?: "Something went wrong",
                            onRetryClick = { shows.retry() }
                        )
                    }
                    else -> {
                        if (shows.itemCount == 0 &&
                            shows.loadState.append is LoadState.NotLoading &&
                            shows.loadState.append.endOfPaginationReached
                        ) {
                            ErrorWidget(
                                modifier = Modifier.fillMaxSize(),
                                message = stringResource(R.string.discover_no_results),
                                onRetryClick = { shows.refresh() }
                            )
                        } else {
                            PaginatedPreviewList(items = shows) { show ->
                                ShowItem(show = show, onShowClick = { onShowClick(show.id) })
                            }
                        }
                    }
                }
            } else {
                val movies = movieItems ?: return@Box
                when (val state = movies.loadState.refresh) {
                    is LoadState.Loading -> {
                        LoadingWidget(modifier = Modifier.fillMaxSize())
                    }
                    is LoadState.Error -> {
                        ErrorWidget(
                            modifier = Modifier.fillMaxSize(),
                            message = state.error.message ?: "Something went wrong",
                            onRetryClick = { movies.retry() }
                        )
                    }
                    else -> {
                        if (movies.itemCount == 0 &&
                            movies.loadState.append is LoadState.NotLoading &&
                            movies.loadState.append.endOfPaginationReached
                        ) {
                            ErrorWidget(
                                modifier = Modifier.fillMaxSize(),
                                message = stringResource(R.string.discover_no_results),
                                onRetryClick = { movies.refresh() }
                            )
                        } else {
                            PaginatedPreviewList(items = movies) { movie ->
                                MovieItem(movie = movie, onMovieClick = { onMovieClick(movie.id) })
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        DiscoverFilterSheet(
            initialFilters = filters,
            genres = genres,
            onApply = { newFilters ->
                onApplyFilters(newFilters)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}
