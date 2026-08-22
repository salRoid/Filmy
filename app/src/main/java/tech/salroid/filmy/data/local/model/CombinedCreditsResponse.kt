package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable as KSerializable
import java.io.Serializable

@KSerializable
data class CombinedCreditsResponse(
    @SerialName("cast")
    var cast: List<CombinedCredit> = emptyList(),

    @SerialName("crew")
    var crew: List<CombinedCredit> = emptyList(),

    @SerialName("id")
    var id: Int? = null
)

@KSerializable
data class CombinedCredit(
    @SerialName("id")
    var id: Int? = null,

    @SerialName("title")
    var title: String? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("poster_path")
    var posterPath: String? = null,

    @SerialName("release_date")
    var releaseDate: String? = null,

    @SerialName("first_air_date")
    var firstAirDate: String? = null,

    @SerialName("character")
    var character: String? = null,

    @SerialName("job")
    var job: String? = null,

    @SerialName("media_type")
    var mediaType: String? = null,

    @SerialName("popularity")
    var popularity: Double? = null
) : Serializable {
    val displayTitle: String? get() = title ?: name
    val displayDate: String? get() = releaseDate ?: firstAirDate
    val isTv: Boolean get() = mediaType == "tv"
    val role: String? get() = character?.takeIf { it.isNotBlank() } ?: job?.takeIf { it.isNotBlank() }
}
