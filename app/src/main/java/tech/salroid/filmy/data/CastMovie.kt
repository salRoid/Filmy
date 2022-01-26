package tech.salroid.filmy.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CastMovie(

    @SerializedName("adult")
    var adult: Boolean? = null,

    @SerializedName("backdrop_path")
    var backdropPath: String? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("genre_ids")
    var genreIds: ArrayList<Int> = arrayListOf(),

    @SerializedName("original_language")
    var originalLanguage: String? = null,

    @SerializedName("original_title")
    var originalTitle: String? = null,

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("video")
    var video: Boolean? = null,

    @SerializedName("vote_average")
    var voteAverage: Double? = null,

    @SerializedName("overview")
    var overview: String? = null,

    @SerializedName("release_date")
    var releaseDate: String? = null,

    @SerializedName("vote_count")
    var voteCount: Int? = null,

    @SerializedName("popularity")
    var popularity: Double? = null,

    @SerializedName("character")
    var character: String? = null,

    @SerializedName("credit_id")
    var creditId: String? = null,

    @SerializedName("order")
    var order: Int? = null

) : Serializable