package tech.salroid.filmy.data.local.model.watch_providers

import com.google.gson.annotations.SerializedName

data class WatchProviderResponse(

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("results")
    var results: Map<String, WatchProviderCountry> = emptyMap()
)