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
    val onBackNavigation: () -> Unit,
    val onAddToListClick: () -> Unit = {},
    val onCollectionClick: (Int, String) -> Unit = { _, _ -> },
    val onSeasonClick: (Int, Int, String) -> Unit = { _, _, _ -> },
    val onRateClick: () -> Unit = {},
    val onGalleryClick: (Int, Boolean) -> Unit = { _, _ -> },
    val onKeywordClick: (Int, String) -> Unit = { _, _ -> },
    val onLinkClick: (String) -> Unit = {}
)
