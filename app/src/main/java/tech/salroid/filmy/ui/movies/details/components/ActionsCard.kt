package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.DetailsActions
import tech.salroid.filmy.ui.common.model.MediaDetailsUiState
import tech.salroid.filmy.ui.common.model.PaletteColors
import tech.salroid.filmy.ui.common.shiftedUntil
import tech.salroid.filmy.ui.theme.AppTheme

// Roughly a 4.5:1 contrast against the near-white / near-black page.
private const val MAX_TINT_LUMINANCE_ON_LIGHT = 0.16f
private const val MIN_TINT_LUMINANCE_ON_DARK = 0.35f

@Composable
fun ActionsCard(
    state: MediaDetailsUiState,
    actions: DetailsActions,
    paletteColors: PaletteColors?,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    // The selected state has to stand out from both the page and the grey
    // unselected items, so the palette colour is taken from its deep end on a
    // light page (and its bright end on a dark one), then pushed further if
    // the image's own tones are too washed out to read.
    val tint = remember(paletteColors, isDark) {
        if (isDark) {
            (paletteColors?.lightVibrantRgb ?: paletteColors?.vibrantRgb)
                ?.let { Color(it).shiftedUntil(Color.White) { c -> c.luminance() >= MIN_TINT_LUMINANCE_ON_DARK } }
        } else {
            (paletteColors?.darkVibrantRgb ?: paletteColors?.vibrantRgb ?: paletteColors?.darkMutedRgb)
                ?.let { Color(it).shiftedUntil(Color.Black) { c -> c.luminance() <= MAX_TINT_LUMINANCE_ON_LIGHT } }
        }
    } ?: MaterialTheme.colorScheme.primary

    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 16.dp,
                bottom = 24.dp,
                start = 16.dp,
                end = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Watched Toggle
        ActionItem(
            isSelected = state.isWatched,
            onIcon = painterResource(R.drawable.ic_check),
            offIcon = painterResource(R.drawable.ic_check),
            label = if (state.isWatched) stringResource(R.string.watched) else stringResource(R.string.watched_question),
            selectedColor = tint,
            unselectedColor = unselectedColor,
            onClick = {
                haptic.performHapticFeedback(
                    if (!state.isWatched) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                )
                actions.onWatchedToggle()
            },
            isToggle = true,
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
            label = if (state.isWatchlist) stringResource(R.string.watchlisted) else stringResource(
                R.string.add_to_watchlist
            ),
            selectedColor = tint,
            unselectedColor = unselectedColor,
            onClick = {
                haptic.performHapticFeedback(
                    if (!state.isWatchlist) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                )
                actions.onWatchlistToggle()
            },
            isToggle = true,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(
            modifier = Modifier.height(24.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        // Rate
        ActionItem(
            isSelected = state.userRating != null,
            onIcon = rememberVectorPainter(Icons.Filled.Star),
            offIcon = rememberVectorPainter(Icons.Outlined.StarBorder),
            label = state.userRating?.let { "★ $it" } ?: stringResource(R.string.rate),
            selectedColor = tint,
            unselectedColor = unselectedColor,
            onClick = actions.onRateClick,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(
            modifier = Modifier.height(24.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )

        // Add to List
        ActionItem(
            isSelected = state.isInList,
            onIcon = painterResource(R.drawable.ic_collections_bookmark_24dp),
            offIcon = painterResource(R.drawable.ic_collections_bookmark_24dp),
            label = stringResource(if (state.isInList) R.string.in_list else R.string.add_to_list),
            selectedColor = tint,
            unselectedColor = unselectedColor,
            onClick = actions.onAddToListClick,
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
    modifier: Modifier = Modifier,
    isToggle: Boolean = false
) {
    val color by animateColorAsState(
        targetValue = if (isSelected) selectedColor else unselectedColor,
        label = "color_anim"
    )

    // Small celebratory bounce when the item actually becomes selected -
    // guarded by hasComposedOnce so an already-selected item (e.g. reopening
    // a watched title) doesn't bounce on first composition.
    var hasComposedOnce by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }
    LaunchedEffect(isSelected) {
        if (isSelected && hasComposedOnce) {
            scale.animateTo(
                1.35f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh)
            )
            scale.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
        hasComposedOnce = true
    }

    Column(
        modifier = modifier
            .then(
                if (isToggle) {
                    Modifier.toggleable(
                        value = isSelected,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        role = Role.Checkbox,
                        onValueChange = { onClick() }
                    )
                } else {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                        role = Role.Button,
                        onClick = onClick
                    )
                }
            )
            .semantics(mergeDescendants = true) {}
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = if (isSelected) onIcon else offIcon,
            contentDescription = null,
            tint = color,
            modifier = Modifier
                .size(22.dp)
                .scale(scale.value)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
