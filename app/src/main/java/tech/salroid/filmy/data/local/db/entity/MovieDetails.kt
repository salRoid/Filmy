package tech.salroid.filmy.data.local.db.entity

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.local.model.Collection

@Entity(tableName = "movie_details", primaryKeys = ["id", "type"])
@Serializable
data class MovieDetails(

    @SerialName("id")
    var id: Int = 0,

    @SerialName("adult")
    var adult: Boolean? = null,

    @SerialName("backdrop_path")
    var backdropPath: String? = null,

    @SerialName("belongs_to_collection")
    var belongsToCollection: Collection? = null,

    @SerialName("budget")
    var budget: Long? = null,

    @SerialName("genres")
    var genres: ArrayList<Genre> = arrayListOf(),

    @SerialName("homepage")
    var homepage: String? = null,

    @SerialName("imdb_id")
    var imdbId: String? = null,

    @SerialName("original_language")
    var originalLanguage: String? = null,

    @SerialName("original_title")
    var originalTitle: String? = null,

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

    @SerialName("release_date")
    var releaseDate: String? = null,

    @SerialName("revenue")
    var revenue: Long? = null,

    @SerialName("runtime")
    var runtime: Int? = null,

    @SerialName("spoken_languages")
    var spokenLanguages: ArrayList<SpokenLanguages> = arrayListOf(),

    @SerialName("status")
    var status: String? = null,

    @SerialName("tagline")
    var tagline: String? = null,

    @SerialName("title")
    var title: String? = null,

    @SerialName("video")
    var video: Boolean? = null,

    @SerialName("vote_average")
    var voteAverage: Double? = null,

    @SerialName("vote_count")
    var voteCount: Long? = null,

    @SerialName("trailers")
    var trailers: Trailers? = null,

    @SerialName("videos")
    var videos: Videos? = null,

    var type: Int = 0,

    var watched: Boolean = false,

    var watchlist: Boolean = false,

    var userRating: Float? = null
)