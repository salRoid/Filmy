package tech.salroid.filmy.ui.shows

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
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.LocalWindowSizeClass
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.common.components.HomeTopBar
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.search.SearchScreenState
import tech.salroid.filmy.ui.shows.components.ShowsList

@Composable
fun ShowsScreen(
    modifier: Modifier = Modifier,
    state: ShowsScreenState,
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onShowClick: (Int) -> Unit,
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
            is ShowsScreenState.Loading -> LoadingWidget(modifier = Modifier.weight(1f))
            is ShowsScreenState.Success -> ShowsList(
                modifier = Modifier.weight(1f),
                shows = state.shows,
                onShowClick = onShowClick
            )

            is ShowsScreenState.Error -> ErrorWidget(
                modifier = Modifier.weight(1f),
                message = state.errorMessage,
                onRetryClick = onRetry
            )
        }
    }
}

// <---------------------- PREVIEWS --------------------------->
private val previewShows = listOf(
    TvShowPreview(
        id = 1,
        title = "Show 1",
        "",
        firstAirReadableDate = "06 Jan 2026"
    ),
    TvShowPreview(
        id = 2,
        title = "Show 2",
        "",
        firstAirReadableDate = "06 Jan 2026"
    ),
    TvShowPreview(
        id = 3,
        title = "Show 3",
        "",
        firstAirReadableDate = "06 Jan 2026"
    )
)

@Preview(
    name = "ShowsScreen – Loading",
    showBackground = true
)
@Composable
fun MoviesScreenLoadingPreview() {
    ShowsScreen(
        modifier = Modifier.fillMaxSize(),
        state = ShowsScreenState.Loading,
        textFieldState = rememberTextFieldState(),
        searchUiState = SearchScreenState.Idle,
        isSearchExpanded = false,
        onSearchExpandedChange = {},
        onSearch = {},
        onShowClick = {},
        onRetry = {}
    )
}

@Preview(
    name = "ShowsScreen – Success",
    showBackground = true
)
@Composable
fun MoviesScreenSuccessPreview() {
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo(
        supportLargeAndXLargeWidth = true
    ).windowSizeClass

    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        ShowsScreen(
            modifier = Modifier.fillMaxSize(),
            state = ShowsScreenState.Success(
                shows = previewShows
            ),
            textFieldState = rememberTextFieldState(),
            searchUiState = SearchScreenState.Idle,
            isSearchExpanded = false,
            onSearchExpandedChange = {},
            onSearch = {},
            onShowClick = {},
            onRetry = {}
        )
    }
}

@Preview(
    name = "ShowsScreen – Error",
    showBackground = true
)
@Composable
fun MoviesScreenErrorPreview() {
    ShowsScreen(
        modifier = Modifier.fillMaxSize(),
        state = ShowsScreenState.Error(
            errorMessage = "Something went wrong. Please try again."
        ),
        textFieldState = rememberTextFieldState(),
        searchUiState = SearchScreenState.Idle,
        isSearchExpanded = false,
        onSearchExpandedChange = {},
        onSearch = {},
        onShowClick = {},
        onRetry = {}
    )
}
