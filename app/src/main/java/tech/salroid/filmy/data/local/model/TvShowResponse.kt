package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class TvShowResponse(

    @SerializedName("page")
    var page: Int? = null,

    @SerializedName("results")
    var results: ArrayList<TvShow> = arrayListOf(),

    @SerializedName("total_results")
    var totalResults: Int? = null,

    @SerializedName("total_pages")
    var totalPages: Int? = null,

    var resetLocal: Boolean = false
)