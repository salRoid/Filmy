package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class FavoriteRequest(
    @SerializedName("media_type")
    var mediaType: String,

    @SerializedName("media_id")
    var mediaId: Int,

    @SerializedName("favorite")
    var favorite: Boolean,
)
