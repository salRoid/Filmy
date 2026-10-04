package tech.salroid.filmy.data.local.db.entity

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "movies", primaryKeys = ["id", "type"])
@Serializable
data class Movie(

    @SerialName("id")
    var id: Int = 0,

    @SerialName("adult")
    var adult: Boolean? = null,

    @SerialName("backdrop_path")
    var backdropPath: String? = null,

    @SerialName("genre_ids")
    var genreIds: ArrayList<Int> = arrayListOf(),

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

    @SerialName("release_date")
    var releaseDate: String? = null,

    @SerialName("title")
    var title: String? = null,

    @SerialName("video")
    var video: Boolean? = null,

    @SerialName("vote_average")
    var voteAverage: Double? = null,

    @SerialName("vote_count")
    var voteCount: Int? = null,

    var type: Int = 0,
) {
    enum class MovieType {
        TRENDING,
        POPULAR,
        NOW_PLAYING,
        UPCOMING,
        TOP_RATED;

        fun toMovieTypeString(): String = when (this) {
            TRENDING -> "movie_trending"
            POPULAR -> "movie_popular"
            NOW_PLAYING -> "movie_now_playing"
            UPCOMING -> "movie_upcoming"
            TOP_RATED -> "movie_top_rated"
        }
    }
}

