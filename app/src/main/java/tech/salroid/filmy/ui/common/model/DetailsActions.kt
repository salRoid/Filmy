package tech.salroid.filmy.ui.common.model

data class DetailsActions(
    val onWatchedToggle: () -> Unit,
    val onWatchlistToggle: () -> Unit,
    val onViewAllCastClick: (Int, Boolean, String) -> Unit,
    val onViewAllReviewsClick: (Int, Boolean, String) -> Unit,
    val onMemberClick: (Int, Boolean) -> Unit,
    val onMediaClick: (Int) -> Unit,
    val onTrailerClick: (String) -> Unit,
    val onShareClick: () -> Unit,
    val onBackNavigation: () -> Unit
)
