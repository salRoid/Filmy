package tech.salroid.filmy.data.local.model.tv

import com.google.gson.annotations.SerializedName

data class SeasonDetailsResponse(

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("overview")
    var overview: String? = null,

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("season_number")
    var seasonNumber: Int? = null,

    @SerializedName("episodes")
    var episodes: List<Episode> = emptyList()
)
