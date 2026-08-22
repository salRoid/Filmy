package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatedResponse(
    @SerialName("page")
    var page: Int? = null,

    @SerialName("results")
    var results: List<RatedItem> = emptyList(),

    @SerialName("total_pages")
    var totalPages: Int? = null,
)

@Serializable
data class RatedItem(
    @SerialName("id")
    var id: Int,

    @SerialName("rating")
    var rating: Float? = null,
)
