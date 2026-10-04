package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.salroid.filmy.data.local.db.entity.Movie

@Serializable
data class MoviesResponse(
    @SerialName("page")
    var page: Int? = null,

    @SerialName("results")
    var results: List<Movie> = listOf(),

    @SerialName("total_pages")
    var totalPages: Int? = null,

    @SerialName("total_results")
    var totalResults: Int? = null,

    var resetLocal: Boolean = false
)