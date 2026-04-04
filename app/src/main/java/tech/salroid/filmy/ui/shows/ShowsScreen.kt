package tech.salroid.filmy.ui.shows

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.common.components.HomeTopBar
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.shows.components.ShowsList
import tech.salroid.filmy.ui.search.SearchScreenState

@Composable
fun ShowsScreen(
    modifier: Modifier = Modifier,
    shows: LazyPagingItems<TvShowPreview>,
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onShowClick: (Int) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeTopBar(
            textFieldState = textFieldState,
            searchUiState = searchUiState,
            isSearchExpanded = isSearchExpanded,
            onSearchExpandedChange = onSearchExpandedChange,
            onSearch = onSearch
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
                        onShowClick = onShowClick
                    )
                }
            }
        }
    }
}
