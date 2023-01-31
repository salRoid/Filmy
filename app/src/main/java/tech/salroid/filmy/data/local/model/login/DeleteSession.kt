package tech.salroid.filmy.data.local.model.login

import com.google.gson.annotations.SerializedName

data class DeleteSession(
    @SerializedName("success")
    var success: Boolean? = null,

    @SerializedName("session_id")
    var sessionId: String? = null,
)