package tech.salroid.filmy.data.local.model.tv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Episode(

    @SerialName("id")
    var id: Int? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("overview")
    var overview: String? = null,

    @SerialName("still_path")
    var stillPath: String? = null,

    @SerialName("air_date")
    var airDate: String? = null,

    @SerialName("episode_number")
    var episodeNumber: Int? = null,

    @SerialName("season_number")
    var seasonNumber: Int? = null,

    @SerialName("runtime")
    var runtime: Int? = null,

    @SerialName("vote_average")
    var voteAverage: Double? = null
)
