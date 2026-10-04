package tech.salroid.filmy.data.local.model.watch_providers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchProviderCountry(

    @SerialName("link")
    var link: String? = null,

    @SerialName("flatrate")
    var flatrate: ArrayList<FlatRate> = arrayListOf(),

    @SerialName("rent")
    var rent: ArrayList<Rent> = arrayListOf(),

    @SerialName("buy")
    var buy: ArrayList<Buy> = arrayListOf()
)