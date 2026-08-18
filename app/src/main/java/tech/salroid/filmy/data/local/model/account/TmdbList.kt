package tech.salroid.filmy.data.local.model.account

import com.google.gson.annotations.SerializedName

data class TmdbList(
    @SerializedName("id")
    var id: Int,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("item_count")
    var itemCount: Int? = null,

    @SerializedName("poster_path")
    var posterPath: String? = null,
)
