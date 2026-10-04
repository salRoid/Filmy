package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbStatusResponse(
    @SerialName("status_code")
    var statusCode: Int? = null,

    @SerialName("status_message")
    var statusMessage: String? = null,
)
