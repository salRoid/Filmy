package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CastCrewMoviesResponse(
    @SerialName("cast")
    var castMovies: ArrayList<CastMovie> = arrayListOf(),

    @SerialName("crew")
    var crewMovies: ArrayList<CrewMovie> = arrayListOf(),

    @SerialName("id")
    var id: Int? = null
)