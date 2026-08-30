package tech.salroid.filmy.core.navigation

import tech.salroid.filmy.R

data class TopLevelDestination(
    val graphRoute: String,
    val startDestinationRoute: String,
    val label: String,
    val iconRes: Int,
    val contentDescription: String
)

val TopLevelDestinations = listOf(
    TopLevelDestination(
        graphRoute = AppRoute.MoviesGraph.route,
        startDestinationRoute = AppRoute.Movies.route,
        label = "Movies",
        iconRes = R.drawable.outline_movie_24,
        contentDescription = "Movies"
    ),
    TopLevelDestination(
        graphRoute = AppRoute.ShowsGraph.route,
        startDestinationRoute = AppRoute.Shows.route,
        label = "Shows",
        iconRes = R.drawable.ic_tv,
        contentDescription = "Shows"
    ),
    TopLevelDestination(
        graphRoute = AppRoute.CollectionGraph.route,
        startDestinationRoute = AppRoute.Collection.route,
        label = "Collection",
        iconRes = R.drawable.ic_collections_bookmark_24dp,
        contentDescription = "Collection"
    ),
    TopLevelDestination(
        graphRoute = AppRoute.AccountGraph.route,
        startDestinationRoute = AppRoute.Account.route,
        label = "Account",
        iconRes = R.drawable.ic_face,
        contentDescription = "Account"
    )
)