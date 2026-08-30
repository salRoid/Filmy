package tech.salroid.filmy.ui.common.model

data class SeasonUiModel(
    val id: Int,
    val seasonNumber: Int,
    val name: String,
    val episodeCount: Int,
    val posterPath: String?,
    val airDate: String?
)
