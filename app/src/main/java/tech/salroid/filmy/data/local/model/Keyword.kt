package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class Keyword(
    @SerializedName("id")
    var id: Int,

    @SerializedName("name")
    var name: String? = null
)
