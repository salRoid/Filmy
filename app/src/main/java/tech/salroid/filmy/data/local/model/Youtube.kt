package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Youtube(

    @SerialName("name")
    var name: String? = null,

    @SerialName("size")
    var size: String? = null,

    @SerialName("source")
    var source: String? = null,

    @SerialName("type")
    var type: String? = null
)