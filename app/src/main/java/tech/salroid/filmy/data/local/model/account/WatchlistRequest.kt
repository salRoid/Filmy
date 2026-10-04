package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class WatchlistRequest(
    @SerializedName("media_type")
    var mediaType: String,

    @SerializedName("media_id")
    var mediaId: Int,

    @SerializedName("watchlist")
    var watchlist: Boolean,
)
