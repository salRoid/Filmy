package tech.salroid.filmy.data.local.model.tv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Seasons(

    @SerialName("air_date")
    var airDate: String? = null,

    @SerialName("episode_count")
    var episodeCount: Int? = null,

    @SerialName("id")
    var id: Int? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("overview")
    var overview: String? = null,

    @SerialName("poster_path")
    var posterPath: String? = null,

    @SerialName("season_number")
    var seasonNumber: Int? = null
)