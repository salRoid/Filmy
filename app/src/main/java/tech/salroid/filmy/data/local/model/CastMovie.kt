package tech.salroid.filmy.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable as KSerializable
import java.io.Serializable

@KSerializable
data class CastMovie(

    @SerialName("adult")
    var adult: Boolean? = null,

    @SerialName("backdrop_path")
    var backdropPath: String? = null,

    @SerialName("id")
    var id: Int? = null,

    @SerialName("genre_ids")
    var genreIds: ArrayList<Int> = arrayListOf(),

    @SerialName("original_language")
    var originalLanguage: String? = null,

    @SerialName("original_title")
    var originalTitle: String? = null,

    @SerialName("poster_path")
    var posterPath: String? = null,

    @SerialName("title")
    var title: String? = null,

    @SerialName("name")
    var name: String? = null,

    @SerialName("video")
    var video: Boolean? = null,

    @SerialName("vote_average")
    var voteAverage: Double? = null,

    @SerialName("overview")
    var overview: String? = null,

    @SerialName("release_date")
    var releaseDate: String? = null,

    @SerialName("vote_count")
    var voteCount: Int? = null,

    @SerialName("popularity")
    var popularity: Double? = null,

    @SerialName("character")
    var character: String? = null,

    @SerialName("credit_id")
    var creditId: String? = null,

    @SerialName("order")
    var order: Int? = null

) : Serializable