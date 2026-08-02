package tech.salroid.filmy.data.local.model.login

import com.google.gson.annotations.SerializedName

data class RequestTokenData(
    @SerializedName("redirect_to")
    var redirectTo: String? = null,

    @SerializedName("request_token")
    var requestToken: String? = null,
)