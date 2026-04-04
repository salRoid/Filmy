package tech.salroid.filmy.ui.common.model

data class ReviewResponseUiModel(
    val results: List<ReviewUiModel>
)

data class ReviewUiModel(
    val id: String,
    val author: String,
    val content: String,
    val createdAt: String,
    val authorAvatarUrl: String?
)
