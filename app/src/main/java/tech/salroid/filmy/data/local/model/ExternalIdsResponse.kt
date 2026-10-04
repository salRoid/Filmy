package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalIdsResponse(
    @SerialName("imdb_id")
    var imdbId: String? = null,

    @SerialName("facebook_id")
    var facebookId: String? = null,

    @SerialName("instagram_id")
    var instagramId: String? = null,

    @SerialName("twitter_id")
    var twitterId: String? = null
)
