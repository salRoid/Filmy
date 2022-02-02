package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class CastMovieDetailsResponse(
    @SerializedName("cast")
    var castMovies: ArrayList<CastMovie> = arrayListOf(),

    @SerializedName("crew")
    var crewMovies: ArrayList<CrewMovie> = arrayListOf(),

    @SerializedName("id")
    var id: Int? = null
)