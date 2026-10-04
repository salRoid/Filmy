package tech.salroid.filmy.data.local.model.tv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Networks(

    @SerialName("name")
    var name: String? = null,

    @SerialName("id")
    var id: Int? = null,

    @SerialName("logo_path")
    var logoPath: String? = null,

    @SerialName("origin_country")
    var originCountry: String? = null
)