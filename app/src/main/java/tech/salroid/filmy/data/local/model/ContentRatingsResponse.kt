package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentRatingsResponse(
    @SerialName("id")
    var id: Int? = null,

    @SerialName("results")
    var results: List<ContentRating> = emptyList(),
)

@Serializable
data class ContentRating(
    @SerialName("iso_3166_1")
    var iso31661: String? = null,

    @SerialName("rating")
    var rating: String? = null,
)
