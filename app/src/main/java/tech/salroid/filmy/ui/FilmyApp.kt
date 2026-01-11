package tech.salroid.filmy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import tech.salroid.filmy.ui.navigation.AppNavHost
import tech.salroid.filmy.ui.navigation.AppNavigationBar
import tech.salroid.filmy.ui.navigation.TopLevelDestinations

@Composable
fun FilmyApp(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo(
        supportLargeAndXLargeWidth = true
    ).windowSizeClass
) {
    val navController = rememberNavController()

    Scaffold(bottomBar = {
        Box(modifier = Modifier.height(90.dp)) { // proper fix needed
            AnimatedVisibility(
                visible = shouldShowNavigationBar(navController),
                enter = fadeIn(animationSpec = ChromeMotionSpec),
                exit = fadeOut(animationSpec = ChromeMotionSpec)
            ) {
                AppNavigationBar(navController)
            }
        }
    }) { paddingValues ->
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

val LocalWindowSizeClass = staticCompositionLocalOf<WindowSizeClass> {
    error("WindowSizeClass not provided")
}

private val ChromeMotionSpec = tween<Float>(
    durationMillis = 500,
    easing = FastOutSlowInEasing
)