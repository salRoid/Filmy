package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName
import tech.salroid.filmy.data.local.db.entity.Movie

data class TmdbListDetailsResponse(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("items")
    var items: List<Movie> = emptyList(),

    @SerializedName("page")
    var page: Int? = null,

    @SerializedName("total_pages")
    var totalPages: Int? = null,
)
