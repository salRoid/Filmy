package tech.salroid.filmy.data.local.model

import com.google.gson.annotations.SerializedName

data class CrewMovie(

    @SerializedName("adult")
    var adult: Boolean? = null,

    @SerializedName("backdrop_path")
    var backdropPath: String? = null,

    @SerializedName("genre_ids")
    var genreIds: ArrayList<Int> = arrayListOf(),

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("original_language")
    var originalLanguage: String? = null,

    @SerializedName("original_title")
    var originalTitle: String? = null,

    @SerializedName("overview")
    var overview: String? = null,

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("release_date")
    var releaseDate: String? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("video")
    var video: Boolean? = null,

    @SerializedName("vote_average")
    var voteAverage: Int? = null,

    @SerializedName("vote_count")
    var voteCount: Int? = null,

    @SerializedName("popularity")
    var popularity: Double? = null,

    @SerializedName("credit_id")
    var creditId: String? = null,

    @SerializedName("department")
    var department: String? = null,

    @SerializedName("job")
    var job: String? = null
)