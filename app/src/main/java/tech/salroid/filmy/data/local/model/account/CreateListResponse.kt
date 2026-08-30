package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class CreateListResponse(
    @SerializedName("list_id")
    var listId: Int? = null,

    @SerializedName("success")
    var success: Boolean? = null,

    @SerializedName("status_code")
    var statusCode: Int? = null,

    @SerializedName("status_message")
    var statusMessage: String? = null,
)
