package tech.salroid.filmy.data

import com.google.gson.annotations.SerializedName

data class Ratings(
    @SerializedName("Source")
    var source: String? = null,

    @SerializedName("Value")
    var value: String? = null
)