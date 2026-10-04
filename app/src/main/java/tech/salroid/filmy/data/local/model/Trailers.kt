package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Trailers(

    @SerialName("quicktime")
    var quicktime: ArrayList<String> = arrayListOf(),

    @SerialName("youtube")
    var youtube: ArrayList<Youtube> = arrayListOf()
)