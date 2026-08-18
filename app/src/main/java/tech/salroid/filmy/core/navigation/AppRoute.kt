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
    object People : AppRoute("people")
    object Account : AppRoute("account")
    object About : AppRoute("about")
    object License : AppRoute("license")

    object AllCastCrew : AppRoute("cast_crew/{id}/{isTv}/{title}") {
        fun create(
            id: Int,
            isTv: Boolean,
            title: String
        ) = "cast_crew/$id/$isTv/$title"
    }

    object CastCrewDetails : AppRoute("cast_crew_details/{memberId}/{isTv}") {
        fun create(
            memberId: Int,
            isTv: Boolean
        ) = "cast_crew_details/$memberId/$isTv"
    }

    object AllMovies : AppRoute("all_movies/{memberId}/{isTv}/{title}") {
        fun create(
            memberId: Int,
            isTv: Boolean,
            title: String
        ) = "all_movies/$memberId/$isTv/$title"
    }

    object ReviewsList : AppRoute("reviews_list/{id}/{isTv}/{title}") {
        fun create(
            id: Int,
            isTv: Boolean,
            title: String
        ) = "reviews_list/$id/$isTv/$title"
    }

    object Discover : AppRoute("discover/{isTv}?keywordId={keywordId}&keywordName={keywordName}") {
        fun create(isTv: Boolean, keywordId: Int? = null, keywordName: String? = null): String {
            val base = "discover/$isTv"
            return if (keywordId != null && keywordName != null) {
                "$base?keywordId=$keywordId&keywordName=$keywordName"
            } else {
                base
            }
        }
    }

    object Onboarding : AppRoute("onboarding")
}
