package tech.salroid.filmy.data.local.model.collection

import com.google.gson.annotations.SerializedName

data class CollectionDetailsResponse(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("overview")
    var overview: String? = null,

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("backdrop_path")
    var backdropPath: String? = null,

    @SerializedName("parts")
    var parts: List<CollectionMovie> = emptyList(),
)
