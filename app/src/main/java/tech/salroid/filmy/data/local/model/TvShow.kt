package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvShow(

    @SerialName("poster_path")
    var posterPath: String? = null,

    @SerialName("popularity")
    var popularity: Double? = null,

    @SerialName("id")
    var id: Int,

    @SerialName("backdrop_path")
    var backdropPath: String? = null,

    @SerialName("vote_average")
    var voteAverage: Double? = null,

    @SerialName("overview")
    var overview: String? = null,

    @SerialName("first_air_date")
    var firstAirDate: String? = null,

    @SerialName("origin_country")
    var originCountry: ArrayList<String> = arrayListOf(),

    @SerialName("genre_ids")
    var genreIds: ArrayList<Int> = arrayListOf(),

    @SerialName("original_language")
    var originalLanguage: String? = null,

    @SerialName("vote_count")
    var voteCount: Int? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("original_name")
    var originalName: String? = null
) {
    enum class ShowType {
        TRENDING,
        POPULAR,
        AIRING_TODAY,
        ON_TV,
        TOP_RATED;

        fun toShowTypeString(): String = when (this) {
            TRENDING -> "tv_show_trending"
            POPULAR -> "tv_show_popular"
            AIRING_TODAY -> "tv_show_airing_today"
            ON_TV -> "tv_show_on_the_air"
            TOP_RATED -> "tv_show_top_rated"
        }
    }
}
