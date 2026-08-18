package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class ContentRatingsResponse(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("results")
    var results: List<ContentRating> = emptyList(),
)

data class ContentRating(
    @SerializedName("iso_3166_1")
    var iso31661: String? = null,

    @SerializedName("rating")
    var rating: String? = null,
)
