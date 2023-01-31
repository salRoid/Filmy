package tech.salroid.filmy.data.local.model.login

import com.google.gson.annotations.SerializedName

data class RequestTokenResponse(

    @SerializedName("status_message")
    var statusMessage: String? = null,

    @SerializedName("request_token")
    var requestToken: String? = null,

    @SerializedName("success")
    var success: Boolean? = null,

    @SerializedName("status_code")
    var statusCode: Int? = null,

    @SerializedName("access_token")
    var accessToken: String? = null,

    @SerializedName("account_id")
    var accountId: String? = null
)