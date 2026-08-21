package tech.salroid.filmy.ui.shows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.TvShow
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.data.model.SearchPreview
import tech.salroid.filmy.ui.common.components.CategorySelector
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.common.components.HomeTopBar
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.common.components.QuickActionState
import tech.salroid.filmy.ui.shows.components.ShowsList
import tech.salroid.filmy.ui.search.SearchScreenState

private fun labelFor(category: TvShow.ShowType): Int = when (category) {
    TvShow.ShowType.TRENDING -> R.string.tv_label_trending
    TvShow.ShowType.POPULAR -> R.string.tv_label_popular
    TvShow.ShowType.AIRING_TODAY -> R.string.tv_label_arriving_today
    TvShow.ShowType.ON_TV -> R.string.tv_label_on_tv
    TvShow.ShowType.TOP_RATED -> R.string.tv_label_top_rated
}

@Composable
fun ShowsScreen(
    modifier: Modifier = Modifier,
    shows: LazyPagingItems<TvShowPreview>,
    selectedCategory: TvShow.ShowType,
    onCategorySelected: (TvShow.ShowType) -> Unit,
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onShowClick: (Int) -> Unit,
    onSearchResultClick: (SearchPreview) -> Unit,
    recentSearches: List<String> = emptyList(),
    onRecentSearchClick: (String) -> Unit = {},
    onRemoveRecentSearch: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    fetchQuickActionState: suspend (TvShowPreview) -> QuickActionState = { QuickActionState(false, false) },
    onQuickToggleWatchlist: (TvShowPreview) -> Unit = {},
    onQuickToggleWatched: (TvShowPreview) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeTopBar(
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
                        categories = TvShow.ShowType.entries,
                        selected = selectedCategory,
                        label = { stringResource(labelFor(it)) },
                        onSelected = onCategorySelected
                    )
                    IconButton(onClick = onFilterClick) {
                        Icon(Icons.Default.Tune, contentDescription = stringResource(R.string.discover_filters))
                    }
                }
            }
        )

        when (val state = shows.loadState.refresh) {
            is LoadState.Loading -> {
                LoadingWidget(modifier = Modifier.weight(1f))
            }
            is LoadState.Error -> {
                ErrorWidget(
                    modifier = Modifier.weight(1f),
                    message = state.error.message ?: "Something went wrong",
                    onRetryClick = { shows.retry() }
                )
            }
            else -> {
                if (shows.itemCount == 0 && shows.loadState.append is LoadState.NotLoading && shows.loadState.append.endOfPaginationReached) {
                    ErrorWidget(
                        modifier = Modifier.weight(1f),
                        message = "No shows found",
                        onRetryClick = { shows.refresh() }
                    )
                } else {
                    ShowsList(
                        modifier = Modifier.weight(1f),
                        shows = shows,
                        onShowClick = onShowClick,
                        fetchQuickActionState = fetchQuickActionState,
                        onQuickToggleWatchlist = onQuickToggleWatchlist,
                        onQuickToggleWatched = onQuickToggleWatched
                    )
                }
            }
        }
    }
}
