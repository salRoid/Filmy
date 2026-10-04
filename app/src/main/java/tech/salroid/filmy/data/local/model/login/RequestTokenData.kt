package tech.salroid.filmy.data.local.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestTokenData(
    @SerialName("redirect_to")
    var redirectTo: String? = null,

    @SerialName("request_token")
    var requestToken: String? = null,
)