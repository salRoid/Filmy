package tech.salroid.filmy.data.local.model.account

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbListsResponse(
    @SerialName("page")
    var page: Int? = null,

    @SerialName("results")
    var results: List<TmdbList> = emptyList(),

    @SerialName("total_pages")
    var totalPages: Int? = null,

    @SerialName("total_results")
    var totalResults: Int? = null,
)
