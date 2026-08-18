package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class RatedResponse(
    @SerializedName("page")
    var page: Int? = null,

    @SerializedName("results")
    var results: List<RatedItem> = emptyList(),

    @SerializedName("total_pages")
    var totalPages: Int? = null,
)

data class RatedItem(
    @SerializedName("id")
    var id: Int,

    @SerializedName("rating")
    var rating: Float? = null,
)
