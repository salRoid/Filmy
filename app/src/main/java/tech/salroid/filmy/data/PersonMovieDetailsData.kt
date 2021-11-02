package tech.salroid.filmy.data

data class PersonMovieDetailsData @JvmOverloads constructor(
    var movieId: String,
    var movieTitle: String? = null,
    var moviePoster: String? = null,
    var rolePlayed: String? = null,
)