package tech.salroid.filmy.data.local.model.tv

import com.google.gson.annotations.SerializedName

data class Networks(

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("logo_path")
    var logoPath: String? = null,

    @SerializedName("origin_country")
    var originCountry: String? = null
)