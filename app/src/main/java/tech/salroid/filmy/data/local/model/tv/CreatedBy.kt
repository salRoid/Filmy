package tech.salroid.filmy.data.local.model.tv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatedBy(

    @SerialName("id")
    var id: Int? = null,

    @SerialName("credit_id")
    var creditId: String? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("gender")
    var gender: Int? = null,

    @SerialName("profile_path")
    var profilePath: String? = null
)