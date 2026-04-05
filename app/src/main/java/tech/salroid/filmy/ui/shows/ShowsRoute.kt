package tech.salroid.filmy.ui.shows

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import tech.salroid.filmy.data.model.SearchPreview
import tech.salroid.filmy.ui.search.SearchScreenState

@Composable
fun ShowsRoute(
    modifier: Modifier = Modifier,
    viewModel: ShowsViewModel = hiltViewModel(),
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onShowClick: (Int) -> Unit,
    onSearchResultClick: (SearchPreview) -> Unit
) {
    val shows = viewModel.showsPagingData.collectAsLazyPagingItems()

    ShowsScreen(
        modifier = modifier,
        shows = shows,
        textFieldState = textFieldState,
        searchUiState = searchUiState,
        isSearchExpanded = isSearchExpanded,
        onSearchExpandedChange = onSearchExpandedChange,
        onSearch = onSearch,
        onShowClick = onShowClick,
        onSearchResultClick = onSearchResultClick
    )
}
