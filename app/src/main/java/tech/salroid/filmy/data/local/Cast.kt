package tech.salroid.filmy.data.local

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Cast(

    @SerializedName("adult")
    var adult: Boolean? = null,

    @SerializedName("gender")
    var gender: Int? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("known_for_department")
    var knownForDepartment: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("original_name")
    var originalName: String? = null,

    @SerializedName("popularity")
    var popularity: Double? = null,

    @SerializedName("profile_path")
    var profilePath: String? = null,

    @SerializedName("cast_id")
    var castId: Int? = null,

    @SerializedName("character")
    var character: String? = null,

    @SerializedName("credit_id")
    var creditId: String? = null,

    @SerializedName("order")
    var order: Int? = null

) : Serializable