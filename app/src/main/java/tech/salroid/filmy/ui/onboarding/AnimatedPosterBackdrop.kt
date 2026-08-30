package tech.salroid.filmy.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val FRAME_DELAY_MS = 16L

/**
 * Immersive hero backdrop: 3 columns of posters, each auto-scrolling
 * continuously at a different speed/direction, forever.
 *
 * Genuinely infinite by construction - each column is a [LazyColumn] with a
 * huge fake item count, indexed onto the real (short) poster list via
 * modulo, so there is no "ran out of content" case to get wrong: LazyColumn
 * only ever composes what's on screen plus a small buffer, regardless of how
 * far the scroll position has moved. Purely decorative - if [posterUrls] is
 * empty (offline first launch, fetch failed, etc.) this just renders a plain
 * surface instead.
 */
@Composable
fun AnimatedPosterBackdrop(
    posterUrls: List<String>,
    modifier: Modifier = Modifier
) {
    if (posterUrls.isEmpty()) {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant))
        return
    }

    val columnCount = 3
    val columns = remember(posterUrls) {
        // Every column draws from the whole poster list (just rotated to a
        // different starting point) rather than a disjoint split, so no
        // column is ever starved of source images.
        val step = (posterUrls.size / columnCount).coerceAtLeast(1)
        (0 until columnCount).map { columnIndex ->
            val rotation = columnIndex * step
            posterUrls.drop(rotation) + posterUrls.take(rotation)
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        columns.forEachIndexed { index, posters ->
            PosterColumn(
                posters = posters,
                pixelsPerFrame = 0.6f + index * 0.3f,
                scrollUp = index % 2 == 0,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun PosterColumn(
    posters: List<String>,
    pixelsPerFrame: Float,
    scrollUp: Boolean,
    modifier: Modifier = Modifier
) {
    // Scrolling "up" only ever increases the scroll position, so index 0 is a
    // safe start. Scrolling "down" decreases it, so it starts deep in the
    // middle of the fake range to leave effectively unlimited headroom.
    val startIndex = if (scrollUp) 0 else Int.MAX_VALUE / 2
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)

    LaunchedEffect(listState, scrollUp, pixelsPerFrame) {
        while (isActive) {
            listState.scrollBy(if (scrollUp) pixelsPerFrame else -pixelsPerFrame)
            delay(FRAME_DELAY_MS)
        }
    }

    LazyColumn(
        state = listState,
        userScrollEnabled = false,
        modifier = modifier
    ) {
        items(count = Int.MAX_VALUE) { index ->
            AsyncImage(
                model = posters[index % posters.size],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
