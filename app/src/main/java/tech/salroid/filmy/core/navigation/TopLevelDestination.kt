package tech.salroid.filmy.core.navigation

import androidx.annotation.StringRes
import tech.salroid.filmy.R

data class TopLevelDestination(
    val graphRoute: String,
    val startDestinationRoute: String,
    @StringRes val labelRes: Int,
    val iconRes: Int
)

val TopLevelDestinations = listOf(
    TopLevelDestination(
        graphRoute = AppRoute.MoviesGraph.route,
        startDestinationRoute = AppRoute.Movies.route,
        labelRes = R.string.tab_movies,
        iconRes = R.drawable.outline_movie_24
    ),
    TopLevelDestination(
        graphRoute = AppRoute.ShowsGraph.route,
        startDestinationRoute = AppRoute.Shows.route,
        labelRes = R.string.tab_shows,
        iconRes = R.drawable.ic_tv
    ),
    TopLevelDestination(
        graphRoute = AppRoute.CollectionGraph.route,
        startDestinationRoute = AppRoute.Collection.route,
        labelRes = R.string.tab_collection,
        iconRes = R.drawable.ic_collections_bookmark_24dp
    ),
    TopLevelDestination(
        graphRoute = AppRoute.AccountGraph.route,
        startDestinationRoute = AppRoute.Account.route,
        labelRes = R.string.tab_account,
        iconRes = R.drawable.ic_face
    )
)
