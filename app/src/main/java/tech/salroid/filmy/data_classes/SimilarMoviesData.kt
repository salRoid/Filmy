package tech.salroid.filmy.data_classes

data class SimilarMoviesData(
    var movieId: String,
    var movieTitle: String? = null,
    var movieBanner: String? = null
)