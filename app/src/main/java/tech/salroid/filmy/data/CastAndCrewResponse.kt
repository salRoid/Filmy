package tech.salroid.filmy.data

import com.google.gson.annotations.SerializedName

data class CastAndCrewResponse(

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("cast")
    var cast: ArrayList<Cast> = arrayListOf(),

    @SerializedName("crew")
    var crew: ArrayList<Crew> = arrayListOf()
)