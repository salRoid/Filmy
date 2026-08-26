package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class OmdbSeasonResponse(

    @SerializedName("Episodes")
    var episodes: List<OmdbEpisodeRating> = emptyList(),

    @SerializedName("Response")
    var response: String? = null
)

data class OmdbEpisodeRating(

    @SerializedName("Episode")
    var episode: Int? = null,

    @SerializedName("imdbRating")
    var imdbRating: String? = null,

    @SerializedName("imdbID")
    var imdbID: String? = null
)
