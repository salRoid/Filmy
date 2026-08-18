package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class ListItemRequest(
    @SerializedName("media_id")
    var mediaId: Int,
)
