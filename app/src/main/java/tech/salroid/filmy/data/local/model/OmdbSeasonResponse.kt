package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OmdbSeasonResponse(

    @SerialName("Episodes")
    var episodes: List<OmdbEpisodeRating> = emptyList(),

    @SerialName("Response")
    var response: String? = null
)

@Serializable
data class OmdbEpisodeRating(

    @SerialName("Episode")
    var episode: Int? = null,

    @SerialName("imdbRating")
    var imdbRating: String? = null,

    @SerialName("imdbID")
    var imdbID: String? = null
)
