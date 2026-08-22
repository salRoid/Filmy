package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CastAndCrewResponse(

    @SerialName("id")
    var id: Int? = null,

    @SerialName("cast")
    var cast: ArrayList<Cast> = arrayListOf(),

    @SerialName("crew")
    var crew: ArrayList<Crew> = arrayListOf()
)