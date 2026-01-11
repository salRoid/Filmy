package tech.salroid.filmy.ui.navigation

sealed class AppRoute(val route: String) {
    // Bottom Tabs (graphs)
    object MoviesGraph : AppRoute("movies_graph")
    object ShowsGraph : AppRoute("shows_graph")
    object CollectionGraph : AppRoute("collection_graph")
    object AccountGraph : AppRoute("account_graph")

    // Screens
    object Movies : AppRoute("movies")

    object MovieDetails : AppRoute("movie/{movieId}") {
        fun create(id: Int) = "movie/$id"
    }
}