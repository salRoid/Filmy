package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName
import tech.salroid.filmy.data.local.db.entity.Movie

data class MoviesResponse(
    @SerializedName("page")
    var page: Int? = null,

    @SerializedName("results")
    var results: ArrayList<Movie> = arrayListOf(),

    @SerializedName("total_pages")
    var totalPages: Int? = null,

    @SerializedName("total_results")
    var totalResults: Int? = null
)