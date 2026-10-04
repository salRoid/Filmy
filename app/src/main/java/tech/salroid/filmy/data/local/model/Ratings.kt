package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Ratings(
    @SerialName("Source")
    var source: String? = null,

    @SerialName("Value")
    var value: String? = null
)