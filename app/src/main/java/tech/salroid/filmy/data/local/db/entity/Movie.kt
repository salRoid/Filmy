package tech.salroid.filmy.data.local.db.entity

import android.content.Context
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.component.NavigationItemData

@Entity(tableName = "movies", primaryKeys = ["id", "type"])
data class Movie(

    @SerializedName("id")
    var id: Int,

    @SerializedName("adult")
    var adult: Boolean? = null,

    @SerializedName("backdrop_path")
    var backdropPath: String? = null,

    @SerializedName("genre_ids")
    var genreIds: ArrayList<Int> = arrayListOf(),

    @SerializedName("original_language")
    var originalLanguage: String? = null,

    @SerializedName("original_title")
    var originalTitle: String? = null,

    @SerializedName("overview")
    var overview: String? = null,

    @SerializedName("popularity")
    var popularity: Double? = null,

    @SerializedName("poster_path")
    var posterPath: String? = null,

    @SerializedName("release_date")
    var releaseDate: String? = null,

    @SerializedName("title")
    var title: String? = null,

    @SerializedName("video")
    var video: Boolean? = null,

    @SerializedName("vote_average")
    var voteAverage: Double? = null,

    @SerializedName("vote_count")
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

fun String.toMovieType() = when (this) {
    "movie_trending" -> Movie.MovieType.TRENDING
    "movie_popular" -> Movie.MovieType.POPULAR
    "movie_now_playing" -> Movie.MovieType.NOW_PLAYING
    "movie_upcoming" -> Movie.MovieType.UPCOMING
    "movie_top_rated" -> Movie.MovieType.TOP_RATED
    else -> null
}

fun getMoviesNavigationList(context: Context) = listOf(
    NavigationItemData(
        tag = Movie.MovieType.TRENDING.toMovieTypeString(),
        label = context.getString(R.string.label_trending)
    ),
    NavigationItemData(
        tag = Movie.MovieType.NOW_PLAYING.toMovieTypeString(),
        label = context.getString(R.string.label_now_playing)
    ),
    NavigationItemData(
        tag = Movie.MovieType.UPCOMING.toMovieTypeString(),
        label = context.getString(R.string.label_upcoming)
    ),
    NavigationItemData(
        tag = Movie.MovieType.POPULAR.toMovieTypeString(),
        label = context.getString(R.string.label_pouplar)
    ),
    NavigationItemData(
        tag = Movie.MovieType.TOP_RATED.toMovieTypeString(),
        label = context.getString(R.string.label_top_rated)
    )
)