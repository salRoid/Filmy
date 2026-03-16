package tech.salroid.filmy.ui.movies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.LocalWindowSizeClass
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.common.components.HomeTopBar
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.movies.components.MoviesList
import tech.salroid.filmy.ui.search.SearchScreenState

@Composable
fun MoviesScreen(
    modifier: Modifier = Modifier,
    state: MoviesScreenState,
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onMovieClick: (Int) -> Unit,
    onRetry: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        HomeTopBar(
            textFieldState = textFieldState,
            searchUiState = searchUiState,
            isSearchExpanded = isSearchExpanded,
            onSearchExpandedChange = onSearchExpandedChange,
            onSearch = onSearch
        )

        when (state) {
            is MoviesScreenState.Loading -> LoadingWidget(modifier = Modifier.weight(1f))
            is MoviesScreenState.Success -> MoviesList(
                modifier = Modifier.weight(1f),
                movies = state.moviesList,
                onMovieClick = onMovieClick
            )

            is MoviesScreenState.Error -> ErrorWidget(
                modifier = Modifier.weight(1f),
                message = state.errorMessage,
                onRetryClick = onRetry
            )
        }
    }
}

// <---------------------- PREVIEWS --------------------------->
private val previewMovies = listOf(
    MoviePreview(
        id = 1,
        title = "Inception",
        "",
        readableReleaseDate = "06 Jan 2026"
    ),
    MoviePreview(
        id = 2,
        title = "Interstellar",
        "",
        readableReleaseDate = "06 Jan 2026"
    ),
    MoviePreview(
        id = 3,
        title = "Dune",
        "",
        readableReleaseDate = "06 Jan 2026"
    )
)

@Preview(
    name = "MoviesScreen – Loading",
    showBackground = true
)
@Composable
fun MoviesScreenLoadingPreview() {
    MoviesScreen(
        modifier = Modifier.fillMaxSize(),
        state = MoviesScreenState.Loading,
        textFieldState = rememberTextFieldState(),
        searchUiState = SearchScreenState.Idle,
        isSearchExpanded = false,
        onSearchExpandedChange = {},
        onSearch = {},
        onMovieClick = {},
        onRetry = {}
    )
}

@Preview(
    name = "MoviesScreen – Success",
    showBackground = true
)
@Composable
fun MoviesScreenSuccessPreview() {
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo(
        supportLargeAndXLargeWidth = true
    ).windowSizeClass

    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        MoviesScreen(
            modifier = Modifier.fillMaxSize(),
            state = MoviesScreenState.Success(
                moviesList = previewMovies
            ),
            textFieldState = rememberTextFieldState(),
            searchUiState = SearchScreenState.Idle,
            isSearchExpanded = false,
            onSearchExpandedChange = {},
            onSearch = {},
            onMovieClick = {},
            onRetry = {}
        )
    }
}

@Preview(
    name = "MoviesScreen – Error",
    showBackground = true
)
@Composable
fun MoviesScreenErrorPreview() {
    MoviesScreen(
        modifier = Modifier.fillMaxSize(),
        state = MoviesScreenState.Error(
            errorMessage = "Something went wrong. Please try again."
        ),
        textFieldState = rememberTextFieldState(),
        searchUiState = SearchScreenState.Idle,
        isSearchExpanded = false,
        onSearchExpandedChange = {},
        onSearch = {},
        onMovieClick = {},
        onRetry = {}
    )
}