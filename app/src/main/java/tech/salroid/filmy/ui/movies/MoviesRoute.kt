package tech.salroid.filmy.ui.movies

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MoviesScreen(
        modifier = modifier,
        state = state,
        textFieldState = textFieldState,
        searchUiState = searchUiState,
        isSearchExpanded = isSearchExpanded,
        onSearchExpandedChange = onSearchExpandedChange,
        onSearch = onSearch,
        onMovieClick = onMovieClick,
        onRetry = viewModel::retry
    )
}
