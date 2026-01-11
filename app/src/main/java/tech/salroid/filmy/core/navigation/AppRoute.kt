package tech.salroid.filmy.core.navigation

sealed class AppRoute(val route: String) {
    // Bottom Tabs (graphs)
    object MoviesGraph : AppRoute("movies_graph")
    object ShowsGraph : AppRoute("shows_graph")
    object CollectionGraph : AppRoute("collection_graph")
    object AccountGraph : AppRoute("account_graph")

    // Screens
    object Movies : AppRoute("movies")

    object MovieDetails : AppRoute("movies/{movieId}") {
        fun create(id: Int) = "movies/$id"
    }

    object Shows : AppRoute("shows")
    object ShowDetails : AppRoute("shows/{showId}") {
        fun create(id: Int) = "shows/$id"
    }

    object Collection : AppRoute("collection")
    object Account : AppRoute("account")
}