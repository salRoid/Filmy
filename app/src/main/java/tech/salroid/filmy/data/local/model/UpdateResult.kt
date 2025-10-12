package tech.salroid.filmy.data.local.model

data class UpdateResult(
    val updatedFavoriteState: Boolean,
    val updatedWatchlistState: Boolean,
    val actionFavorite: Boolean,
    val actionWatchlist: Boolean
)