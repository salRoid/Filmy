package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class RatingRequest(
    @SerializedName("value")
    var value: Float
)
