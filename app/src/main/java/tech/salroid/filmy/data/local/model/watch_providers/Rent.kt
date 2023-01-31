package tech.salroid.filmy.data.local.model.watch_providers

import com.google.gson.annotations.SerializedName

data class Rent(

    @SerializedName("display_priority")
    var displayPriority: Int? = null,

    @SerializedName("logo_path")
    var logoPath: String? = null,

    @SerializedName("provider_id")
    var providerId: Int? = null,

    @SerializedName("provider_name")
    var providerName: String? = null
)