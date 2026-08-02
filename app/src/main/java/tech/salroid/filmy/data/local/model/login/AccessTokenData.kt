package tech.salroid.filmy.data.local.model.login

import com.google.gson.annotations.SerializedName

data class AccessTokenData(
    @SerializedName("access_token")
    var accessToken: String? = null,
)