package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class TmdbListDetailsResponse(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("results")
    var items: List<TmdbListItem> = emptyList(),

    @SerializedName("page")
    var page: Int? = null,

    @SerializedName("total_pages")
    var totalPages: Int? = null,
)
