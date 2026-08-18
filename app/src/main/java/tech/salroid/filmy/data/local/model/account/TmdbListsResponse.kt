package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class TmdbListsResponse(
    @SerializedName("page")
    var page: Int? = null,

    @SerializedName("results")
    var results: List<TmdbList> = emptyList(),

    @SerializedName("total_pages")
    var totalPages: Int? = null,

    @SerializedName("total_results")
    var totalResults: Int? = null,
)
