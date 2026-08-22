package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvShowResponse(

    @SerialName("page")
    var page: Int? = null,

    @SerialName("results")
    var results: ArrayList<TvShow> = arrayListOf(),

    @SerialName("total_results")
    var totalResults: Int? = null,

    @SerialName("total_pages")
    var totalPages: Int? = null,

    var resetLocal: Boolean = false
)