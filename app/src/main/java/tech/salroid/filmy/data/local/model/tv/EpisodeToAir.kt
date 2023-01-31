package tech.salroid.filmy.data.local.model.tv

import com.google.gson.annotations.SerializedName

data class EpisodeToAir(

    @SerializedName("air_date")
    var airDate: String? = null,

    @SerializedName("episode_number")
    var episodeNumber: Int? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("overview")
    var overview: String? = null,

    @SerializedName("production_code")
    var productionCode: String? = null,

    @SerializedName("season_number")
    var seasonNumber: Int? = null,

    @SerializedName("still_path")
    var stillPath: String? = null,

    @SerializedName("vote_average")
    var voteAverage: Double? = null,

    @SerializedName("vote_count")
    var voteCount: Int? = null
)