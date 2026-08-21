package tech.salroid.filmy.ui.common.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.movies.dummyShowPreview

/** Current watchlist/watched state for a grid item's long-press quick-actions. */
data class QuickActionState(
    val isWatchlisted: Boolean,
    val isWatched: Boolean
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviewItem(
    modifier: Modifier = Modifier,
    title: String,
    posterUrl: String,
    readableDate: String,
    contentDescription: String? = null,
    onItemClick: () -> Unit,
    fetchQuickActionState: (suspend () -> QuickActionState)? = null,
    onToggleWatchlist: (() -> Unit)? = null,
    onToggleWatched: (() -> Unit)? = null
) {
    val hasQuickActions = onToggleWatchlist != null || onToggleWatched != null
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    var isFlipped by remember { mutableStateOf(false) }
    var quickActionState by remember { mutableStateOf<QuickActionState?>(null) }

    LaunchedEffect(isFlipped) {
        if (isFlipped && fetchQuickActionState != null) {
            quickActionState = fetchQuickActionState()
        }
    }

    val rotation = animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(400),
        label = "poster_flip"
    )
    val showBack by remember { derivedStateOf { rotation.value > 90f } }

    Card(
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Transparent)
    ) {
        Column(
            modifier = modifier.combinedClickable(
                onClick = {
                    if (isFlipped) isFlipped = false else onItemClick()
                },
                onLongClick = if (hasQuickActions) {
                    // combinedClickable already performs LongPress haptic itself
                    // when onLongClick fires - no need to call it again here.
                    { isFlipped = true }
                } else null
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .graphicsLayer {
                        rotationY = rotation.value
                        cameraDistance = 12f * density.density
                    }
                    .clip(RoundedCornerShape(corner = CornerSize(8.dp)))
            ) {
                if (!showBack) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        model = posterUrl,
                        contentDescription = contentDescription
                    )
                } else {
                    val isWatchlisted = quickActionState?.isWatchlisted == true
                    val isWatched = quickActionState?.isWatched == true

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationY = 180f }
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isFlipped = false
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(24.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                        ) {
                            if (onToggleWatchlist != null) {
                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(
                                            if (!isWatchlisted) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                                        )
                                        quickActionState = (quickActionState ?: QuickActionState(false, false))
                                            .copy(isWatchlisted = !isWatchlisted)
                                        onToggleWatchlist()
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            if (isWatchlisted) R.drawable.ic_round_bookmark_added_24
                                            else R.drawable.ic_round_bookmark_add_24
                                        ),
                                        contentDescription = if (isWatchlisted) "Remove from Watchlist" else "Add to Watchlist",
                                        tint = if (isWatchlisted) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (onToggleWatched != null) {
                                IconButton(
                                    onClick = {
                                        haptic.performHapticFeedback(
                                            if (!isWatched) HapticFeedbackType.ToggleOn else HapticFeedbackType.ToggleOff
                                        )
                                        quickActionState = (quickActionState ?: QuickActionState(false, false))
                                            .copy(isWatched = !isWatched)
                                        onToggleWatched()
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = if (isWatched) "Mark as Unwatched" else "Mark Watched",
                                        tint = if (isWatched) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Text(
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                text = title
            )
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(0.8f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                text = readableDate
            )
        }
    }
}

@Preview(
    name = "PreviewItem - Preview",
    showBackground = true
)
@Composable
fun MovieItemPreview() {
    PreviewItem(
        modifier = Modifier
            .width(140.dp)
            .padding(16.dp),
        title = dummyShowPreview.title,
        posterUrl = dummyShowPreview.posterUrl,
        readableDate = dummyShowPreview.firstAirReadableDate,
        onItemClick = { }
    )
}
