package tech.salroid.filmy.ui.common.model

data class DetailsActions(
    val onFavoriteToggle: () -> Unit,
    val onWatchlistToggle: () -> Unit,
    val onViewAllCastClick: (Int, Boolean, String) -> Unit,
    val onMemberClick: (Int, Boolean) -> Unit,
    val onMediaClick: (Int) -> Unit,
    val onBackNavigation: () -> Unit
)