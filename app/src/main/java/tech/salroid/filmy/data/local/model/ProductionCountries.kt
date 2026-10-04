package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductionCountries(

    @SerialName("iso_3166_1")
    var iso31661: String? = null,

    @SerialName("name")
    var name: String? = null
)