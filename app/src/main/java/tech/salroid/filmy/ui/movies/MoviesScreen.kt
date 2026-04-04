package tech.salroid.filmy.ui.movies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.common.components.HomeTopBar
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.movies.components.MoviesList
import tech.salroid.filmy.ui.search.SearchScreenState

@Composable
fun MoviesScreen(
    modifier: Modifier = Modifier,
    movies: LazyPagingItems<MoviePreview>,
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onMovieClick: (Int) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeTopBar(
            textFieldState = textFieldState,
            searchUiState = searchUiState,
            isSearchExpanded = isSearchExpanded,
            onSearchExpandedChange = onSearchExpandedChange,
            onSearch = onSearch
        )

        when (val state = movies.loadState.refresh) {
            is LoadState.Loading -> {
                LoadingWidget(modifier = Modifier.weight(1f))
            }

            is LoadState.Error -> {
                ErrorWidget(
                    modifier = Modifier.weight(1f),
                    message = state.error.message ?: "Something went wrong",
                    onRetryClick = { movies.retry() }
                )
            }

            else -> {
                if (movies.itemCount == 0 && movies.loadState.append is LoadState.NotLoading && movies.loadState.append.endOfPaginationReached) {
                    // Empty state
                    ErrorWidget(
                        modifier = Modifier.weight(1f),
                        message = "No movies found",
                        onRetryClick = { movies.refresh() }
                    )
                } else {
                    MoviesList(
                        modifier = Modifier.weight(1f),
                        movies = movies,
                        onMovieClick = onMovieClick
                    )
                }
            }
        }
    }
}
