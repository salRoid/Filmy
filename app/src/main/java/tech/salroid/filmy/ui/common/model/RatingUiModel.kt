package tech.salroid.filmy.ui.common.model

data class RatingsUiModel(
    val ratings: List<RatingSourceUiModel>
)

data class RatingSourceUiModel(
    val source: RatingSource,
    val value: String,
    val url: String? = null
)

enum class RatingSource {
    TMDB,
    IMDB,
    ROTTEN_TOMATOES,
    METACRITIC,
    OTHER
}
