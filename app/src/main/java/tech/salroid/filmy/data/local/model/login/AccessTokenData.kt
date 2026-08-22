package tech.salroid.filmy.data.local.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenData(
    @SerialName("access_token")
    var accessToken: String? = null,
)