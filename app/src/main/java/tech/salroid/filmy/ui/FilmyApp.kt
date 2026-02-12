package tech.salroid.filmy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.distinctUntilChanged
import tech.salroid.filmy.core.navigation.AppNavHost
import tech.salroid.filmy.core.navigation.AppRoute
import tech.salroid.filmy.core.navigation.components.AppNavigationBar
import tech.salroid.filmy.core.navigation.TopLevelDestinations
import tech.salroid.filmy.ui.component.AppBranding
import tech.salroid.filmy.ui.search.SearchScreenState
import tech.salroid.filmy.ui.search.SearchViewModel
import tech.salroid.filmy.ui.search.component.AppSearchBar

@Composable
fun FilmyApp(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo(
        supportLargeAndXLargeWidth = true
    ).windowSizeClass,
    searchViewModel: SearchViewModel = viewModel()
) {
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    val textFieldState = remember { TextFieldState() }

    val searchQuery by searchViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchUiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    var isSearchExpanded by rememberSaveable { mutableStateOf(false) }

    // VM - UI
    LaunchedEffect(searchQuery) {
        if (textFieldState.text.toString() != searchQuery) {
            textFieldState.setTextAndPlaceCursorAtEnd(searchQuery)
        }
    }

    // TextFieldState → VM
    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .distinctUntilChanged()
            .collect(searchViewModel::onSearchQueryChange)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            AnimatedVisibility(
                visible = shouldShowSearchBar(navController),
                enter = fadeIn(animationSpec = ChromeMotionSpec),
                exit = fadeOut(animationSpec = ChromeMotionSpec)
            ) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    AnimatedVisibility(
                        visible = !isSearchExpanded,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        AppBranding()
                    }

                    AppSearchBar(
                        textFieldState = textFieldState,
                        onSearch = { searchViewModel.onSearchQueryChange(it) },
                        searchResults = when (searchUiState) {
                            is SearchScreenState.Success ->
                                (searchUiState as SearchScreenState.Success).previews

                            else -> persistentListOf()
                        },
                        expanded = isSearchExpanded,
                        onExpandedChange = { isSearchExpanded = it }
                    )
                }
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowNavigationBar(navController),
                enter = fadeIn(animationSpec = ChromeMotionSpec),
                exit = fadeOut(animationSpec = ChromeMotionSpec)
            ) {
                AppNavigationBar(navController)
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalWindowSizeClass provides windowSizeClass
        ) {
            AppNavHost(
                navController = navController,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun shouldShowNavigationBar(navController: NavController): Boolean {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destinationRoute = backStackEntry?.destination?.route

    return TopLevelDestinations
        .any { it.startDestinationRoute == destinationRoute }
}

@Composable
private fun shouldShowSearchBar(navController: NavController): Boolean {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destinationRoute = backStackEntry?.destination?.route

    return destinationRoute == AppRoute.Movies.route || destinationRoute == AppRoute.Shows.route
}

val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass> {
    error("WindowSizeClass not provided")
}

private val ChromeMotionSpec = tween<Float>(
    durationMillis = 500,
    easing = FastOutSlowInEasing
)