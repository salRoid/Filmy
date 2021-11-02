package tech.salroid.filmy.data_classes

data class PersonMovieDetailsData(
    var movieId: String,
    var movieTitle: String? = null,
    var moviePoster: String? = null,
    var rolePlayed: String? = null,
)