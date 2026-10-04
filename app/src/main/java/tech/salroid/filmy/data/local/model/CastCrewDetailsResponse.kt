package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CastCrewDetailsResponse(

    @SerialName("adult")
    var adult: Boolean? = null,

    @SerialName("also_known_as")
    var alsoKnownAs: ArrayList<String> = arrayListOf(),

    @SerialName("biography")
    var biography: String? = null,

    @SerialName("birthday")
    var birthday: String? = null,

    @SerialName("deathday")
    var deathday: String? = null,

    @SerialName("gender")
    var gender: Int? = null,

    @SerialName("homepage")
    var homepage: String? = null,

    @SerialName("id")
    var id: Int? = null,

    @SerialName("imdb_id")
    var imdbId: String? = null,

    @SerialName("known_for_department")
    var knownForDepartment: String? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("place_of_birth")
    var placeOfBirth: String? = null,

    @SerialName("popularity")
    var popularity: Double? = null,

    @SerialName("profile_path")
    var profilePath: String? = null

)