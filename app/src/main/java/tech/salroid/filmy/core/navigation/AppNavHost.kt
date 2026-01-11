package tech.salroid.filmy.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import tech.salroid.filmy.ui.movies.MoviesRoute
import tech.salroid.filmy.ui.movies.details.MovieDetailsScreen
import tech.salroid.filmy.ui.shows.ShowsRoute
import tech.salroid.filmy.ui.shows.details.ShowDetailsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppRoute.MoviesGraph.route,
    ) {
        moviesGraph(navController)
        showsGraph(navController)
        collectionGraph()
        accountGraph()
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

private fun NavGraphBuilder.collectionGraph() {
    navigation(
        route = AppRoute.CollectionGraph.route,
        startDestination = AppRoute.Collection.route
    ) {
        composable(AppRoute.Collection.route) { MoviesRoute { } }
    }
}

private fun NavGraphBuilder.accountGraph() {
    navigation(
        route = AppRoute.AccountGraph.route,
        startDestination = AppRoute.Account.route
    ) {
        composable(AppRoute.Account.route) { MoviesRoute { } }
    }
}