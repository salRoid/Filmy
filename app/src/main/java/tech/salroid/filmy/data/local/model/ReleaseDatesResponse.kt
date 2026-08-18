package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class ReleaseDatesResponse(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("results")
    var results: List<CountryReleaseDates> = emptyList(),
)

data class CountryReleaseDates(
    @SerializedName("iso_3166_1")
    var iso31661: String? = null,

    @SerializedName("release_dates")
    var releaseDates: List<ReleaseDateEntry> = emptyList(),
)

data class ReleaseDateEntry(
    @SerializedName("certification")
    var certification: String? = null,

    @SerializedName("type")
    var type: Int? = null,
)
