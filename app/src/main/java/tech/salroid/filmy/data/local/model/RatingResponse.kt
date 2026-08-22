package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RatingResponse(

    @SerialName("Title")
    var title: String? = null,

    @SerialName("Year")
    var year: String? = null,

    @SerialName("Rated")
    var rated: String? = null,

    @SerialName("Released")
    var released: String? = null,

    @SerialName("Runtime")
    var runtime: String? = null,

    @SerialName("Genre")
    var genre: String? = null,

    @SerialName("Director")
    var director: String? = null,

    @SerialName("Writer")
    var writer: String? = null,

    @SerialName("Actors")
    var actors: String? = null,

    @SerialName("Plot")
    var plot: String? = null,

    @SerialName("Language")
    var language: String? = null,

    @SerialName("Country")
    var country: String? = null,

    @SerialName("Awards")
    var awards: String? = null,

    @SerialName("Poster")
    var poster: String? = null,

    @SerialName("Ratings")
    var ratings: ArrayList<Ratings> = arrayListOf(),

    @SerialName("Metascore")
    var metascore: String? = null,

    @SerialName("imdbRating")
    var imdbRating: String? = null,

    @SerialName("imdbVotes")
    var imdbVotes: String? = null,

    @SerialName("imdbID")
    var imdbID: String? = null,

    @SerialName("Type")
    var type: String? = null,

    @SerialName("tomatoMeter")
    var tomatoMeter: String? = null,

    @SerialName("tomatoImage")
    var tomatoImage: String? = null,

    @SerialName("tomatoRating")
    var tomatoRating: String? = null,

    @SerialName("tomatoReviews")
    var tomatoReviews: String? = null,

    @SerialName("tomatoFresh")
    var tomatoFresh: String? = null,

    @SerialName("tomatoRotten")
    var tomatoRotten: String? = null,

    @SerialName("tomatoConsensus")
    var tomatoConsensus: String? = null,

    @SerialName("tomatoUserMeter")
    var tomatoUserMeter: String? = null,

    @SerialName("tomatoUserRating")
    var tomatoUserRating: String? = null,

    @SerialName("tomatoUserReviews")
    var tomatoUserReviews: String? = null,

    @SerialName("tomatoURL")
    var tomatoURL: String? = null,

    @SerialName("DVD")
    var dvd: String? = null,

    @SerialName("BoxOffice")
    var boxOffice: String? = null,

    @SerialName("Production")
    var production: String? = null,

    @SerialName("Website")
    var website: String? = null,

    @SerialName("Response")
    var response: String? = null
)