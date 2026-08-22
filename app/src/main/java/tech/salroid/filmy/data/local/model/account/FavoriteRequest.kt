package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteRequest(
    @SerialName("media_type")
    var mediaType: String,

    @SerialName("media_id")
    var mediaId: Int,

    @SerialName("favorite")
    var favorite: Boolean,
)
