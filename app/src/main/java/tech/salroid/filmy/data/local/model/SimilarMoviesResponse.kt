package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SimilarMoviesResponse(

    @SerialName("page")
    var page: Int? = null,

    @SerialName("results")
    var results: ArrayList<SimilarMovie> = arrayListOf(),

    @SerialName("total_pages")
    var totalPages: Int? = null,

    @SerialName("total_results")
    var totalResults: Int? = null
)