package tech.salroid.filmy.data.local.model.watch_providers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Buy(

    @SerialName("display_priority")
    var displayPriority: Int? = null,

    @SerialName("logo_path")
    var logoPath: String? = null,

    @SerialName("provider_id")
    var providerId: Int? = null,

    @SerialName("provider_name")
    var providerName: String? = null
)