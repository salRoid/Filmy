package tech.salroid.filmy.ui.movies

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
fun MoviesRoute(
    modifier: Modifier = Modifier,
    viewModel: MoviesViewModel = hiltViewModel(),
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onMovieClick: (Int) -> Unit,
    onSearchResultClick: (SearchPreview) -> Unit,
    onFilterClick: () -> Unit = {},
    onPeopleClick: () -> Unit = {}
) {
    val movies = viewModel.moviesPagingData.collectAsLazyPagingItems()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    MoviesScreen(
        modifier = modifier,
        movies = movies,
        selectedCategory = selectedCategory,
        onCategorySelected = viewModel::onCategorySelected,
        textFieldState = textFieldState,
        searchUiState = searchUiState,
        isSearchExpanded = isSearchExpanded,
        onSearchExpandedChange = onSearchExpandedChange,
        onSearch = onSearch,
        onMovieClick = onMovieClick,
        onSearchResultClick = onSearchResultClick,
        onFilterClick = onFilterClick,
        onPeopleClick = onPeopleClick
    )
}
