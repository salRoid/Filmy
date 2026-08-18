package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R

private data class ExternalLink(val label: String, val url: String, val iconRes: Int?)

private const val GRID_COLUMNS = 2

/** Grid of tappable link tiles (official site, IMDb, socials) for the currently-open title. */
@Composable
fun ExternalLinksSection(
    homepage: String?,
    imdbId: String?,
    facebookId: String?,
    instagramId: String?,
    twitterId: String?,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val links = buildList {
        homepage?.let { add(ExternalLink("Website", it, R.drawable.ic_language_24dp)) }
        imdbId?.let { add(ExternalLink("IMDb", "https://www.imdb.com/title/$it/", R.drawable.imdb)) }
        facebookId?.let { add(ExternalLink("Facebook", "https://www.facebook.com/$it", null)) }
        instagramId?.let { add(ExternalLink("Instagram", "https://www.instagram.com/$it", null)) }
        twitterId?.let { add(ExternalLink("X", "https://x.com/$it", null)) }
    }
    if (links.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        DottedDivider(modifier = Modifier.padding(vertical = 16.dp))

        links.chunked(GRID_COLUMNS).forEach { rowLinks ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowLinks.forEach { link ->
                    LinkTile(link = link, onLinkClick = onLinkClick, modifier = Modifier.weight(1f))
                }
                if (rowLinks.size < GRID_COLUMNS) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DottedDivider(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 4f,
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(1f, 10f), 0f)
        )
    }
}

@Composable
private fun LinkTile(link: ExternalLink, onLinkClick: (String) -> Unit, modifier: Modifier = Modifier) {
    val mutedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onLinkClick(link.url) }
            .padding(vertical = 13.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (link.iconRes != null) {
            Image(
                painter = painterResource(link.iconRes),
                contentDescription = null,
                alpha = 0.7f,
                modifier = Modifier.size(14.dp)
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                contentDescription = null,
                tint = mutedColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = link.label,
            style = MaterialTheme.typography.bodySmall,
            color = mutedColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = mutedColor.copy(alpha = 0.5f),
            modifier = Modifier
                .size(12.dp)
                .graphicsLayer { rotationZ = -45f }
        )
    }
}
