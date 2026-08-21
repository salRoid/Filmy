package tech.salroid.filmy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import kotlinx.coroutines.flow.distinctUntilChanged
import tech.salroid.filmy.core.navigation.AppNavHost
import tech.salroid.filmy.core.navigation.AppNavigationBar
import tech.salroid.filmy.core.navigation.AppRoute
import tech.salroid.filmy.core.navigation.TopLevelDestinations
import tech.salroid.filmy.ui.search.SearchViewModel
import tech.salroid.filmy.utility.PreferenceHelper

@Composable
fun FilmyApp(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo(
        supportLargeAndXLargeWidth = true
    ).windowSizeClass,
    searchViewModel: SearchViewModel = viewModel(),
    throughShortcut: Boolean = false
) {
    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    val textFieldState = remember { TextFieldState() }
    val context = LocalContext.current
    val startDestination = remember {
        when {
            PreferenceHelper.isColdStart(context) -> AppRoute.Onboarding.route
            throughShortcut -> AppRoute.CollectionGraph.route
            else -> AppRoute.MoviesGraph.route
        }
    }

    val searchQuery by searchViewModel.searchQuery.collectAsStateWithLifecycle()
    val searchUiState by searchViewModel.uiState.collectAsStateWithLifecycle()
    val recentSearches by searchViewModel.recentSearches.collectAsStateWithLifecycle()

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

    CompositionLocalProvider(
        LocalWindowSizeClass provides windowSizeClass
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackBarHostState) }
        ) { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
            ) {
                AppNavHost(
                    navController = navController,
                    textFieldState = textFieldState,
                    searchUiState = searchUiState,
                    isSearchExpanded = isSearchExpanded,
                    onSearchExpandedChange = { isSearchExpanded = it },
                    onSearch = {
                        searchViewModel.onSearchQueryChange(it)
                        searchViewModel.commitSearch(it)
                    },
                    recentSearches = recentSearches,
                    onRecentSearchClick = { searchViewModel.onSearchQueryChange(it) },
                    onRemoveRecentSearch = { searchViewModel.removeRecentSearch(it) },
                    onClearRecentSearches = { searchViewModel.clearRecentSearches() },
                    bottomPadding = 80.dp, // Height of NavigationBar
                    modifier = Modifier.fillMaxSize(),
                    startDestination = startDestination
                )

                AnimatedVisibility(
                    visible = shouldShowNavigationBar(navController),
                    enter = fadeIn(animationSpec = ChromeMotionSpec) + slideInVertically(
                        animationSpec = ChromeOffsetMotionSpec,
                        initialOffsetY = { it }
                    ),
                    exit = fadeOut(animationSpec = ChromeMotionSpec) + slideOutVertically(
                        animationSpec = ChromeOffsetMotionSpec,
                        targetOffsetY = { it }
                    ),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    AppNavigationBar(navController)
                }
            }
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

val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass> {
    error("WindowSizeClass not provided")
}

private val ChromeMotionSpec = tween<Float>(
    durationMillis = 500,
    easing = FastOutSlowInEasing
)

private val ChromeOffsetMotionSpec = tween<IntOffset>(
    durationMillis = 500,
    easing = FastOutSlowInEasing
)
