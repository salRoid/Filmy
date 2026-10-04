package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CombinedCreditsResponse(
    @SerializedName("cast")
    var cast: List<CombinedCredit> = emptyList(),

    @SerializedName("crew")
    var crew: List<CombinedCredit> = emptyList(),

    @SerializedName("id")
    var id: Int? = null
)

data class CombinedCredit(
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("release_date")
    var releaseDate: String? = null,

    @SerializedName("first_air_date")
    var firstAirDate: String? = null,

    @SerializedName("character")
    var character: String? = null,

    @SerializedName("job")
    var job: String? = null,

    @SerializedName("media_type")
    var mediaType: String? = null,

    @SerializedName("popularity")
    var popularity: Double? = null
) : Serializable {
    val displayTitle: String? get() = title ?: name
    val displayDate: String? get() = releaseDate ?: firstAirDate
    val isTv: Boolean get() = mediaType == "tv"
    val role: String? get() = character?.takeIf { it.isNotBlank() } ?: job?.takeIf { it.isNotBlank() }
}
