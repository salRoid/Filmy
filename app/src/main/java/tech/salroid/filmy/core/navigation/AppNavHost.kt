package tech.salroid.filmy.core.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import tech.salroid.filmy.ui.account.AccountScreen
import tech.salroid.filmy.ui.collections.CollectionScreen
import tech.salroid.filmy.ui.movies.MoviesRoute
import tech.salroid.filmy.ui.movies.details.MovieDetailsScreen
import tech.salroid.filmy.ui.shows.ShowsRoute
import tech.salroid.filmy.ui.shows.details.ShowDetailsScreen

typealias ShowSnackBar = (String) -> Unit

@Composable
fun AppNavHost(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val showSnackBar: ShowSnackBar = { message ->
        scope.launch {
            snackBarHostState.showSnackbar(message)
        }
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppRoute.MoviesGraph.route,
    ) {
        moviesGraph(navController)
        showsGraph(navController)
        collectionGraph(showSnackBar)
        accountGraph(showSnackBar)
    }
}

private fun NavGraphBuilder.moviesGraph(navController: NavHostController) {
    navigation(
        startDestination = AppRoute.Movies.route,
        route = AppRoute.MoviesGraph.route
    ) {
        composable(AppRoute.Movies.route) {
            MoviesRoute { id ->
                navController.navigate(AppRoute.MovieDetails.create(id))
            }
        }
        composable(
            route = AppRoute.MovieDetails.route,
            arguments = listOf(navArgument("movieId") {
                type = NavType.IntType
            })
        ) {
            MovieDetailsScreen(movieId = it.arguments?.getInt("movieId") ?: 0) {
                navController.popBackStack()
            }
        }
    }
}

private fun NavGraphBuilder.showsGraph(navController: NavHostController) {
    navigation(
        route = AppRoute.ShowsGraph.route,
        startDestination = AppRoute.Shows.route
    ) {
        composable(AppRoute.Shows.route) {
            ShowsRoute { id ->
                navController.navigate(AppRoute.ShowDetails.create(id))
            }
        }
        composable(
            route = AppRoute.ShowDetails.route,
            arguments = listOf(navArgument("showId") {
                type = NavType.IntType
            })
        ) {
            ShowDetailsScreen(showId = it.arguments?.getInt("showId") ?: 0) {
                navController.popBackStack()
            }
        }
    }
}

private fun NavGraphBuilder.collectionGraph(showSnackBar: ShowSnackBar) {
    navigation(
        route = AppRoute.CollectionGraph.route,
        startDestination = AppRoute.Collection.route
    ) {
        composable(AppRoute.Collection.route) {
            CollectionScreen(showSnackBar)
        }
    }
}

private fun NavGraphBuilder.accountGraph(showSnackBar: ShowSnackBar) {
    navigation(
        route = AppRoute.AccountGraph.route,
        startDestination = AppRoute.Account.route
    ) {
        composable(AppRoute.Account.route) {
            AccountScreen(showSnackBar)
        }
    }
}