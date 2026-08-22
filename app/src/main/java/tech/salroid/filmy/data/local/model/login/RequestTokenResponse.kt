package tech.salroid.filmy.data.local.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTokenResponse(

    @SerialName("status_message")
    var statusMessage: String? = null,

    @SerialName("request_token")
    var requestToken: String? = null,

    @SerialName("success")
    var success: Boolean? = null,

    @SerialName("status_code")
    var statusCode: Int? = null,

    @SerialName("access_token")
    var accessToken: String? = null,

    @SerialName("account_id")
    var accountId: String? = null
)