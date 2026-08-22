package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbListDetailsResponse(
    @SerialName("id")
    var id: Int? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("description")
    var description: String? = null,

    @SerialName("results")
    var items: List<TmdbListItem> = emptyList(),

    @SerialName("page")
    var page: Int? = null,

    @SerialName("total_pages")
    var totalPages: Int? = null,
)
