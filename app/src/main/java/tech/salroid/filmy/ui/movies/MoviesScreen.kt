package tech.salroid.filmy.ui.movies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import tech.salroid.filmy.ui.movies.components.MoviesList
import tech.salroid.filmy.ui.search.SearchScreenState

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
    onSearchResultClick: (SearchPreview) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeTopBar(
            textFieldState = textFieldState,
            searchUiState = searchUiState,
            isSearchExpanded = isSearchExpanded,
            onSearchExpandedChange = onSearchExpandedChange,
            onSearch = onSearch,
            onSearchResultClick = onSearchResultClick,
            trailingContent = {
                CategorySelector(
                    categories = Movie.MovieType.entries,
                    selected = selectedCategory,
                    label = { stringResource(labelFor(it)) },
                    onSelected = onCategorySelected
                )
            }
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
                        onMovieClick = onMovieClick
                    )
                }
            }
        }
    }
}
