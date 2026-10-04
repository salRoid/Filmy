package tech.salroid.filmy.ui.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.data.model.SearchPreview
import tech.salroid.filmy.ui.common.components.CategorySelector
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.common.components.HomeTopBar
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.common.components.QuickActionState
import tech.salroid.filmy.ui.common.icons.DropdownMenuIcon
import tech.salroid.filmy.ui.movies.components.MoviesList
import tech.salroid.filmy.ui.search.SearchScreenState
import tech.salroid.filmy.utility.toUserMessage

private fun labelFor(category: Movie.MovieType): Int = when (category) {
    Movie.MovieType.TRENDING -> R.string.label_trending
    Movie.MovieType.POPULAR -> R.string.label_pouplar
    Movie.MovieType.NOW_PLAYING -> R.string.label_now_playing
    Movie.MovieType.UPCOMING -> R.string.label_upcoming
    Movie.MovieType.TOP_RATED -> R.string.label_top_rated
}

@Composable
fun MoviesScreen(
    modifier: Modifier = Modifier,
    movies: LazyPagingItems<MoviePreview>,
    selectedCategory: Movie.MovieType,
    onCategorySelected: (Movie.MovieType) -> Unit,
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onMovieClick: (Int) -> Unit,
    onSearchResultClick: (SearchPreview) -> Unit,
    recentSearches: List<String> = emptyList(),
    onRecentSearchClick: (String) -> Unit = {},
    onRemoveRecentSearch: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onPeopleClick: () -> Unit = {},
    fetchQuickActionState: suspend (MoviePreview) -> QuickActionState = { QuickActionState(false, false) },
    onQuickToggleWatchlist: (MoviePreview) -> Unit = {},
    onQuickToggleWatched: (MoviePreview) -> Unit = {}
) {
    var showMoreMenu by remember { mutableStateOf(false) }

    HomeTopBar(
        modifier = modifier,
        textFieldState = textFieldState,
        searchUiState = searchUiState,
        isSearchExpanded = isSearchExpanded,
        onSearchExpandedChange = onSearchExpandedChange,
        onSearch = onSearch,
        onSearchResultClick = onSearchResultClick,
        recentSearches = recentSearches,
        onRecentSearchClick = onRecentSearchClick,
        onRemoveRecentSearch = onRemoveRecentSearch,
        onClearRecentSearches = onClearRecentSearches,
        trailingContent = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategorySelector(
                        categories = Movie.MovieType.entries,
                        selected = selectedCategory,
                        label = { stringResource(labelFor(it)) },
                        onSelected = onCategorySelected
                    )
                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(
                                DropdownMenuIcon,
                                contentDescription = "More options"
                            )
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Discover Movies") },
                                onClick = {
                                    showMoreMenu = false
                                    onFilterClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Popular People") },
                                onClick = {
                                    showMoreMenu = false
                                    onPeopleClick()
                                }
                            )
                        }
                    }
                }
            },
        content = {
            when (val state = movies.loadState.refresh) {
                is LoadState.Loading -> {
                    LoadingWidget(modifier = Modifier.weight(1f))
                }

                is LoadState.Error -> {
                    ErrorWidget(
                        modifier = Modifier.weight(1f),
                        message = state.error.toUserMessage(),
                        onRetryClick = { movies.retry() }
                    )
                }

                else -> {
                    if (movies.itemCount == 0 &&
                        movies.loadState.append is LoadState.NotLoading &&
                        movies.loadState.append.endOfPaginationReached
                    ) {
                        ErrorWidget(
                            modifier = Modifier.weight(1f),
                            message = "No movies found",
                            onRetryClick = { movies.refresh() }
                        )
                    } else {
                        MoviesList(
                            modifier = Modifier.weight(1f),
                            movies = movies,
                            onMovieClick = onMovieClick,
                            fetchQuickActionState = fetchQuickActionState,
                            onQuickToggleWatchlist = onQuickToggleWatchlist,
                            onQuickToggleWatched = onQuickToggleWatched
                        )
                    }
                }
            }
        }
    )
}
