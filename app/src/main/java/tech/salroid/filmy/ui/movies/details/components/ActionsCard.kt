package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.DetailsActions
import tech.salroid.filmy.ui.common.model.MediaDetailsUiState
import tech.salroid.filmy.ui.common.model.PaletteColors
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun ActionsCard(
    state: MediaDetailsUiState,
    actions: DetailsActions,
    paletteColors: PaletteColors?,
    modifier: Modifier = Modifier
) {
    val tint = paletteColors?.vibrantRgb?.let { Color(it) }
        ?: MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

        // Watched Toggle
        ActionItem(
            isSelected = state.isWatched,
            onIcon = painterResource(R.drawable.ic_check),
            offIcon = painterResource(R.drawable.ic_check),
            label = if (state.isWatched) stringResource(R.string.watched) else stringResource(R.string.watched_question),
            selectedColor = tint,
            unselectedColor = unselectedColor,
            onClick = actions.onWatchedToggle,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(
            modifier = Modifier.height(24.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        // Watchlist Toggle
        ActionItem(
            isSelected = state.isWatchlist,
            onIcon = painterResource(R.drawable.ic_round_bookmark_added_24),
            offIcon = painterResource(R.drawable.ic_round_bookmark_add_24),
            label = if (state.isWatchlist) stringResource(R.string.watchlisted) else stringResource(R.string.add_to_watchlist),
            selectedColor = tint,
            unselectedColor = unselectedColor,
            onClick = actions.onWatchlistToggle,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionItem(
    isSelected: Boolean,
    onIcon: Painter,
    offIcon: Painter,
    label: String,
    selectedColor: Color,
    unselectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        label = "color_anim"
    )

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = if (isSelected) onIcon else offIcon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ActionsCardPreview() {
    AppTheme {
        ActionsCard(
            state = MediaDetailsUiState(
                mediaId = 1,
                title = "Inception",
                overview = "",
                tagline = "",
                backdropPath = null,
                posterPath = null,
                genres = "",
                runtimeText = "",
                releaseDateText = "",
                youtubeTrailers = null,
                isWatched = false,
                isWatchlist = true,
                isTvShow = false
            ),
            actions = DetailsActions(
                onWatchedToggle = {},
                onWatchlistToggle = {},
                onViewAllCastClick = { _, _, _ -> },
                onViewAllReviewsClick = { _, _, _ -> },
                onMemberClick = { _, _ -> },
                onMediaClick = {},
                onTrailerClick = {},
                onShareClick = {},
                onBackNavigation = {}
            ),
            paletteColors = null
        )
    }
}
