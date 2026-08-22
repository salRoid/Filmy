package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReleaseDatesResponse(
    @SerialName("id")
    var id: Int? = null,

    @SerialName("results")
    var results: List<CountryReleaseDates> = emptyList(),
)

@Serializable
data class CountryReleaseDates(
    @SerialName("iso_3166_1")
    var iso31661: String? = null,

    @SerialName("release_dates")
    var releaseDates: List<ReleaseDateEntry> = emptyList(),
)

@Serializable
data class ReleaseDateEntry(
    @SerialName("certification")
    var certification: String? = null,

    @SerialName("type")
    var type: Int? = null,
)
