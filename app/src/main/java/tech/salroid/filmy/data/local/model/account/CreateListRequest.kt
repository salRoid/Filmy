package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class CreateListRequest(
    @SerializedName("name")
    var name: String,

    @SerializedName("description")
    var description: String = "",

    @SerializedName("language")
    var language: String = "en",
)
