package tech.salroid.filmy.data.local.model.tv

import com.google.gson.annotations.SerializedName

data class CreatedBy(

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("credit_id")
    var creditId: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("gender")
    var gender: Int? = null,

    @SerializedName("profile_path")
    var profilePath: String? = null
)