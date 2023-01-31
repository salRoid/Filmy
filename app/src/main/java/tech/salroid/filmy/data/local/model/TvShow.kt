package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class TvShow(

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("popularity")
    var popularity: Double? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("backdrop_path")
    var backdropPath: String? = null,

    @SerializedName("vote_average")
    var voteAverage: Double? = null,

    @SerializedName("overview")
    var overview: String? = null,

    @SerializedName("first_air_date")
    var firstAirDate: String? = null,

    @SerializedName("origin_country")
    var originCountry: ArrayList<String> = arrayListOf(),

    @SerializedName("genre_ids")
    var genreIds: ArrayList<Int> = arrayListOf(),

    @SerializedName("original_language")
    var originalLanguage: String? = null,

    @SerializedName("vote_count")
    var voteCount: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("original_name")
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

fun String.toShowType() = when (this) {
    "tv_show_trending" -> TvShow.ShowType.TRENDING
    "tv_show_popular" -> TvShow.ShowType.POPULAR
    "tv_show_airing_today" -> TvShow.ShowType.AIRING_TODAY
    "tv_show_on_the_air" -> TvShow.ShowType.ON_TV
    "tv_show_top_rated" -> TvShow.ShowType.TOP_RATED
    else -> null
}