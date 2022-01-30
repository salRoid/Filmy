package tech.salroid.filmy.data.local

data class WatchlistData @JvmOverloads constructor(
    var id: String,
    var title: String? = null,
    var poster: String? = null
)