package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpokenLanguages(

    @SerialName("english_name")
    var englishName: String? = null,

    @SerialName("iso_639_1")
    var iso6391: String? = null,

    @SerialName("name")
    var name: String? = null
)