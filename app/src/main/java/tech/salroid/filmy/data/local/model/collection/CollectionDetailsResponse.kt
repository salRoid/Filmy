package tech.salroid.filmy.data.local.model.collection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionDetailsResponse(
    @SerialName("id")
    var id: Int? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("overview")
    var overview: String? = null,

    @SerialName("poster_path")
    var posterPath: String? = null,

    @SerialName("backdrop_path")
    var backdropPath: String? = null,

    @SerialName("parts")
    var parts: List<CollectionMovie> = emptyList(),
)
