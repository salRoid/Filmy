package tech.salroid.filmy.data.local.model.watch_providers

import com.google.gson.annotations.SerializedName

data class WatchProviderCountry(

    @SerializedName("link")
    var link: String? = null,

    @SerializedName("flatrate")
    var flatrate: ArrayList<FlatRate> = arrayListOf(),

    @SerializedName("rent")
    var rent: ArrayList<Rent> = arrayListOf(),

    @SerializedName("buy")
    var buy: ArrayList<Buy> = arrayListOf()
)