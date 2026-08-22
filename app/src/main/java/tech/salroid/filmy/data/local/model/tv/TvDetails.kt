package tech.salroid.filmy.data.local.model.tv

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.salroid.filmy.data.local.model.Genre
import tech.salroid.filmy.data.local.model.ProductionCompanies
import tech.salroid.filmy.data.local.model.ProductionCountries
import tech.salroid.filmy.data.local.model.SpokenLanguages
import tech.salroid.filmy.data.local.model.Trailers
import tech.salroid.filmy.data.local.model.Videos

@Serializable
data class TvDetails(

    @SerialName("backdrop_path")
    var backdropPath: String? = null,

    @SerialName("created_by")
    var createdBy: ArrayList<CreatedBy> = arrayListOf(),

    @SerialName("episode_run_time")
    var episodeRunTime: ArrayList<Int> = arrayListOf(),

    @SerialName("first_air_date")
    var firstAirDate: String? = null,

    @SerialName("genres")
    var genres: ArrayList<Genre> = arrayListOf(),

    @SerialName("homepage")
    var homepage: String? = null,

    @SerialName("id")
    var id: Int? = null,

    @SerialName("in_production")
    var inProduction: Boolean? = null,

    @SerialName("languages")
    var languages: ArrayList<String> = arrayListOf(),

    @SerialName("last_air_date")
    var lastAirDate: String? = null,

    @SerialName("last_episode_to_air")
    var lastEpisodeToAir: EpisodeToAir? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("next_episode_to_air")
    var nextEpisodeToAir: EpisodeToAir? = null,

    @SerialName("networks")
    var networks: ArrayList<Networks> = arrayListOf(),

    @SerialName("number_of_episodes")
    var numberOfEpisodes: Int? = null,

    @SerialName("number_of_seasons")
    var numberOfSeasons: Int? = null,

    @SerialName("origin_country")
    var originCountry: ArrayList<String> = arrayListOf(),

    @SerialName("original_language")
    var originalLanguage: String? = null,

    @SerialName("original_name")
    var originalName: String? = null,

    @SerialName("overview")
    var overview: String? = null,

    @SerialName("popularity")
    var popularity: Double? = null,

    @SerialName("poster_path")
    var posterPath: String? = null,

    @SerialName("production_companies")
    var productionCompanies: ArrayList<ProductionCompanies> = arrayListOf(),

    @SerialName("production_countries")
    var productionCountries: ArrayList<ProductionCountries> = arrayListOf(),

    @SerialName("seasons")
    var seasons: ArrayList<Seasons> = arrayListOf(),

    @SerialName("spoken_languages")
    var spokenLanguages: ArrayList<SpokenLanguages> = arrayListOf(),

    @SerialName("status")
    var status: String? = null,

    @SerialName("tagline")
    var tagline: String? = null,

    @SerialName("type")
    var type: String? = null,

    @SerialName("vote_average")
    var voteAverage: Double? = null,

    @SerialName("vote_count")
    var voteCount: Int? = null,

    @SerialName("trailers")
    var trailers: Trailers? = null,

    @SerialName("videos")
    var videos: Videos? = null
)