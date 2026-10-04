package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewResponse(

    @SerialName("id")
    var id: Int? = null,

    @SerialName("page")
    var page: Int? = null,

    @SerialName("results")
    var results: ArrayList<Review> = arrayListOf(),

    @SerialName("total_pages")
    var totalPages: Int? = null,

    @SerialName("total_results")
    var totalResults: Int? = null
)