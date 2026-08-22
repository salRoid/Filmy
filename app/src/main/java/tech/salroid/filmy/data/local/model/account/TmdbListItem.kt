package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** One entry of a TMDB v4 list: a movie (title / release_date) or a show (name / first_air_date). */
@Serializable
data class TmdbListItem(
    @SerialName("id")
    var id: Int,

    @SerialName("media_type")
    var mediaType: String? = null,

    @SerialName("title")
    var title: String? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("poster_path")
    var posterPath: String? = null,

    @SerialName("release_date")
    var releaseDate: String? = null,

    @SerialName("first_air_date")
    var firstAirDate: String? = null,
) {
    val isTv: Boolean get() = mediaType == ListItemRef.MEDIA_TYPE_TV
    val displayTitle: String get() = title ?: name ?: ""
    val displayDate: String? get() = releaseDate ?: firstAirDate
}
