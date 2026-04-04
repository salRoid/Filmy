package tech.salroid.filmy.core.navigation

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import tech.salroid.filmy.ui.account.AccountScreen
import tech.salroid.filmy.ui.cast_crew.AllCastCrewScreen
import tech.salroid.filmy.ui.cast_crew.CastCrewDetailsScreen
import tech.salroid.filmy.ui.collections.CollectionScreen
import tech.salroid.filmy.ui.full.AllMoviesScreen
import tech.salroid.filmy.ui.movies.MoviesRoute
import tech.salroid.filmy.ui.movies.details.MovieDetailsScreen
import tech.salroid.filmy.ui.reviews.ReviewsListScreen
import tech.salroid.filmy.ui.search.SearchScreenState
import tech.salroid.filmy.ui.settings.AboutScreen
import tech.salroid.filmy.ui.settings.LicenseScreen
import tech.salroid.filmy.ui.shows.ShowsRoute
import tech.salroid.filmy.ui.shows.details.ShowDetailsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = AppRoute.MoviesGraph.route,
    ) {
        moviesGraph(
            navController,
            textFieldState,
            searchUiState,
            isSearchExpanded,
            onSearchExpandedChange,
            onSearch,
            bottomPadding
        )
        showsGraph(
            navController,
            textFieldState,
            searchUiState,
            isSearchExpanded,
            onSearchExpandedChange,
            onSearch,
            bottomPadding
        )
        collectionGraph(navController, bottomPadding)
        accountGraph(navController, bottomPadding)
        commonScreens(navController)
    }
}

private fun NavGraphBuilder.moviesGraph(
    navController: NavHostController,
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    bottomPadding: Dp
) {
    navigation(
        startDestination = AppRoute.Movies.route,
        route = AppRoute.MoviesGraph.route
    ) {
        composable(AppRoute.Movies.route) {
            MoviesRoute(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = bottomPadding),
                textFieldState = textFieldState,
                searchUiState = searchUiState,
                isSearchExpanded = isSearchExpanded,
                onSearchExpandedChange = onSearchExpandedChange,
                onSearch = onSearch,
                onMovieClick = { id ->
                    navController.navigate(AppRoute.MovieDetails.create(id))
                }
            )
        }
        composable(
            route = AppRoute.MovieDetails.route,
            arguments = listOf(navArgument("movieId") {
                type = NavType.IntType
            })
        ) {
            MovieDetailsScreen(
                movieId = it.arguments?.getInt("movieId") ?: 0,
                onBackNavigation = { navController.popBackStack() },
                onViewAllCastClick = { id, isTv, title ->
                    navController.navigate(AppRoute.AllCastCrew.create(id, isTv, title))
                },
                onViewAllReviewsClick = { id, isTv, title ->
                    navController.navigate(AppRoute.ReviewsList.create(id, isTv, title))
                },
                onMemberClick = { memberId, isTv ->
                    navController.navigate(AppRoute.CastCrewDetails.create(memberId, isTv))
                },
                onMovieClick = { id ->
                    navController.navigate(AppRoute.MovieDetails.create(id))
                }
            )
        }
    }
}

private fun NavGraphBuilder.showsGraph(
    navController: NavHostController,
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    bottomPadding: Dp
) {
    navigation(
        route = AppRoute.ShowsGraph.route,
        startDestination = AppRoute.Shows.route
    ) {
        composable(AppRoute.Shows.route) {
            ShowsRoute(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = bottomPadding),
                textFieldState = textFieldState,
                searchUiState = searchUiState,
                isSearchExpanded = isSearchExpanded,
                onSearchExpandedChange = onSearchExpandedChange,
                onSearch = onSearch,
                onShowClick = { id ->
                    navController.navigate(AppRoute.ShowDetails.create(id))
                }
            )
        }
        composable(
            route = AppRoute.ShowDetails.route,
            arguments = listOf(navArgument("showId") {
                type = NavType.IntType
            })
        ) {
            ShowDetailsScreen(
                showId = it.arguments?.getInt("showId") ?: 0,
                onBackNavigation = { navController.popBackStack() },
                onViewAllCastClick = { id, isTv, title ->
                    navController.navigate(AppRoute.AllCastCrew.create(id, isTv, title))
                },
                onViewAllReviewsClick = { id, isTv, title ->
                    navController.navigate(AppRoute.ReviewsList.create(id, isTv, title))
                },
                onMemberClick = { memberId, isTv ->
                    navController.navigate(AppRoute.CastCrewDetails.create(memberId, isTv))
                },
                onShowClick = { id ->
                    navController.navigate(AppRoute.ShowDetails.create(id))
                }
            )
        }
    }
}

private fun NavGraphBuilder.collectionGraph(navController: NavHostController, bottomPadding: Dp) {
    navigation(
        route = AppRoute.CollectionGraph.route,
        startDestination = AppRoute.Collection.route
    ) {
        composable(AppRoute.Collection.route) {
            CollectionScreen(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = bottomPadding)
            ) { id, type ->
                if (type == 1) {
                    navController.navigate(AppRoute.ShowDetails.create(id))
                } else {
                    navController.navigate(AppRoute.MovieDetails.create(id))
                }
            }
        }
    }
}

private fun NavGraphBuilder.accountGraph(navController: NavHostController, bottomPadding: Dp) {
    navigation(
        route = AppRoute.AccountGraph.route,
        startDestination = AppRoute.Account.route
    ) {
        composable(AppRoute.Account.route) {
            AccountScreen(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(bottom = bottomPadding),
                onAboutClick = { navController.navigate(AppRoute.About.route) },
                onLicenseClick = { navController.navigate(AppRoute.License.route) }
            )
        }
        composable(AppRoute.About.route) {
            AboutScreen { navController.popBackStack() }
        }
        composable(AppRoute.License.route) {
            LicenseScreen { navController.popBackStack() }
        }
    }
}

private fun NavGraphBuilder.commonScreens(navController: NavHostController) {
    composable(
        route = AppRoute.AllCastCrew.route,
        arguments = listOf(
            navArgument("id") { type = NavType.IntType },
            navArgument("isTv") { type = NavType.BoolType },
            navArgument("title") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val id = backStackEntry.arguments?.getInt("id") ?: 0
        val isTv = backStackEntry.arguments?.getBoolean("isTv") ?: false
        val title = backStackEntry.arguments?.getString("title") ?: ""
        AllCastCrewScreen(
            id = id,
            isTv = isTv,
            title = title,
            onMemberClick = { memberId ->
                navController.navigate(AppRoute.CastCrewDetails.create(memberId, isTv))
            },
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(
        route = AppRoute.CastCrewDetails.route,
        arguments = listOf(
            navArgument("memberId") { type = NavType.IntType },
            navArgument("isTv") { type = NavType.BoolType }
        )
    ) { backStackEntry ->
        val memberId = backStackEntry.arguments?.getInt("memberId") ?: 0
        val isTv = backStackEntry.arguments?.getBoolean("isTv") ?: false
        CastCrewDetailsScreen(
            memberId = memberId,
            isTv = isTv,
            onMovieClick = { id, _ ->
                if (isTv) {
                    navController.navigate(AppRoute.ShowDetails.create(id))
                } else {
                    navController.navigate(AppRoute.MovieDetails.create(id))
                }
            },
            onViewAllMoviesClick = { id, tv, title ->
                navController.navigate(AppRoute.AllMovies.create(id, tv, title))
            },
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(
        route = AppRoute.AllMovies.route,
        arguments = listOf(
            navArgument("memberId") { type = NavType.IntType },
            navArgument("isTv") { type = NavType.BoolType },
            navArgument("title") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val memberId = backStackEntry.arguments?.getInt("memberId") ?: 0
        val isTv = backStackEntry.arguments?.getBoolean("isTv") ?: false
        val title = backStackEntry.arguments?.getString("title") ?: ""
        AllMoviesScreen(
            memberId = memberId,
            isTv = isTv,
            title = title,
            onMovieClick = { id, _ ->
                if (isTv) {
                    navController.navigate(AppRoute.ShowDetails.create(id))
                } else {
                    navController.navigate(AppRoute.MovieDetails.create(id))
                }
            },
            onBackClick = { navController.popBackStack() }
        )
    }

    composable(
        route = AppRoute.ReviewsList.route,
        arguments = listOf(
            navArgument("id") { type = NavType.IntType },
            navArgument("isTv") { type = NavType.BoolType },
            navArgument("title") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val id = backStackEntry.arguments?.getInt("id") ?: 0
        val isTv = backStackEntry.arguments?.getBoolean("isTv") ?: false
        val title = backStackEntry.arguments?.getString("title") ?: ""
        ReviewsListScreen(
            id = id,
            isTv = isTv,
            title = title,
            onBackClick = { navController.popBackStack() }
        )
    }
}
