package tech.salroid.filmy.data

import com.google.gson.annotations.SerializedName

data class CastDetailsResponse(

    @SerializedName("adult")
    var adult: Boolean? = null,

    @SerializedName("also_known_as")
    var alsoKnownAs: ArrayList<String> = arrayListOf(),

    @SerializedName("biography")
    var biography: String? = null,

    @SerializedName("birthday")
    var birthday: String? = null,

    @SerializedName("deathday")
    var deathday: String? = null,

    @SerializedName("gender")
    var gender: Int? = null,

    @SerializedName("homepage")
    var homepage: String? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("imdb_id")
    var imdbId: String? = null,

    @SerializedName("known_for_department")
    var knownForDepartment: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("place_of_birth")
    var placeOfBirth: String? = null,

    @SerializedName("popularity")
    var popularity: Double? = null,

    @SerializedName("profile_path")
    var profilePath: String? = null

)