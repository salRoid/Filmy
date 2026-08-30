package tech.salroid.filmy.data.local.model.collection

import com.google.gson.annotations.SerializedName

data class CollectionMovie(
    @SerializedName("id")
    var id: Int,

    @SerializedName("name")
    var title: String? = null,

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("release_date")
    var releaseDate: String? = null,
)
