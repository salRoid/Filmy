package tech.salroid.filmy.data

data class SimilarMoviesData @JvmOverloads constructor(
    var id: String,
    var title: String? = null,
    var banner: String? = null
)