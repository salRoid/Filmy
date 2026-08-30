package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class TmdbStatusResponse(
    @SerializedName("status_code")
    var statusCode: Int? = null,

    @SerializedName("status_message")
    var statusMessage: String? = null,
)
