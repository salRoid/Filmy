package tech.salroid.filmy.data.local

import com.google.gson.annotations.SerializedName

data class SearchResultResponse(
    @SerializedName("page")
    var page: Int? = null,

    @SerializedName("results")
    var results: ArrayList<SearchResult> = arrayListOf(),

    @SerializedName("total_pages")
    var totalPages: Int? = null,

    @SerializedName("total_results")
    var totalResults: Int? = null
)