package tech.salroid.filmy.data.local.model.login

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteSession(
    @SerialName("success")
    var success: Boolean? = null,

    @SerialName("session_id")
    var sessionId: String? = null,
)