package tech.salroid.filmy.data

data class SearchData @JvmOverloads constructor(
    var id: String,
    var movie: String? = null,
    var poster: String? = null,
    var type: String? = null,
    var date: String? = null,
    var extra: String? = null
)