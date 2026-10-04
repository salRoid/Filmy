package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatingRequest(
    @SerialName("value")
    var value: Float
)
