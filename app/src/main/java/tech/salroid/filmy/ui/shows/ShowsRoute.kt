package tech.salroid.filmy.ui.shows

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    onSearchResultClick: (SearchPreview) -> Unit,
    recentSearches: List<String> = emptyList(),
    onRecentSearchClick: (String) -> Unit = {},
    onRemoveRecentSearch: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onFilterClick: () -> Unit = {}
) {
    val shows = viewModel.showsPagingData.collectAsLazyPagingItems()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    ShowsScreen(
        modifier = modifier,
        shows = shows,
        selectedCategory = selectedCategory,
        onCategorySelected = viewModel::onCategorySelected,
        textFieldState = textFieldState,
        searchUiState = searchUiState,
        isSearchExpanded = isSearchExpanded,
        onSearchExpandedChange = onSearchExpandedChange,
        onSearch = onSearch,
        onShowClick = onShowClick,
        onSearchResultClick = onSearchResultClick,
        recentSearches = recentSearches,
        onRecentSearchClick = onRecentSearchClick,
        onRemoveRecentSearch = onRemoveRecentSearch,
        onClearRecentSearches = onClearRecentSearches,
        onFilterClick = onFilterClick,
        fetchQuickActionState = viewModel::getQuickActionState,
        onQuickToggleWatchlist = viewModel::quickToggleWatchlist,
        onQuickToggleWatched = viewModel::quickToggleWatched
    )
}
