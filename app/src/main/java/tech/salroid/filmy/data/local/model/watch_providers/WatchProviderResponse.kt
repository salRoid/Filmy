package tech.salroid.filmy.data.local.model.watch_providers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchProviderResponse(

    @SerialName("id")
    var id: Int? = null,

    @SerialName("results")
    var results: Map<String, WatchProviderCountry> = emptyMap()
)