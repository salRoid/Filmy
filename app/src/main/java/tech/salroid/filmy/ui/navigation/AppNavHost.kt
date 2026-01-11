package tech.salroid.filmy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import tech.salroid.filmy.ui.movies.MovieDetails
import tech.salroid.filmy.ui.movies.MoviesScreen

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
        showsGraph()
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
            MoviesScreen { id ->
                navController.navigate(AppRoute.MovieDetails.create(id))
            }
        }
        composable(
            route = AppRoute.MovieDetails.route,
            arguments = listOf(navArgument("movieId") {
                type = NavType.IntType
            })
        ) {
            MovieDetails(movieId = it.arguments?.getInt("movieId") ?: 1234) {
                navController.popBackStack()
            }
        }
    }
}

private fun NavGraphBuilder.showsGraph() {
    navigation(
        route = AppRoute.ShowsGraph.route,
        startDestination = "shows"
    ) {
        composable("shows") { MoviesScreen { } }
    }
}

private fun NavGraphBuilder.collectionGraph() {
    navigation(
        route = AppRoute.CollectionGraph.route,
        startDestination = "collection"
    ) {
        composable("collection") { MoviesScreen { } }
    }
}

private fun NavGraphBuilder.accountGraph() {
    navigation(
        route = AppRoute.AccountGraph.route,
        startDestination = "account"
    ) {
        composable("account") { MoviesScreen { } }
    }
}