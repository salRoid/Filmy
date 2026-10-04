package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

/** One entry of a TMDB v4 list: a movie (title / release_date) or a show (name / first_air_date). */
data class TmdbListItem(
    @SerializedName("id")
    var id: Int,

    @SerializedName("media_type")
    var mediaType: String? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("release_date")
    var releaseDate: String? = null,

    @SerializedName("first_air_date")
    var firstAirDate: String? = null,
) {
    val isTv: Boolean get() = mediaType == ListItemRef.MEDIA_TYPE_TV
    val displayTitle: String get() = title ?: name ?: ""
    val displayDate: String? get() = releaseDate ?: firstAirDate
}
