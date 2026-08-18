package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.PaletteColors
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun DetailsInfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.alpha(0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/** Same dynamic-palette background derivation used by [RatingCard]/[CollectionTeaserRow]. */
@Composable
fun rememberPaletteCardColor(paletteColors: PaletteColors?, fallback: Color): Color {
    val isDark = isSystemInDarkTheme()
    return remember(paletteColors, isDark, fallback) {
        val colorInt = if (isDark) {
            paletteColors?.darkMutedRgb ?: paletteColors?.darkVibrantRgb
        } else {
            paletteColors?.lightMutedRgb ?: paletteColors?.lightVibrantRgb
        }
        colorInt?.let { Color(it).copy(alpha = 0.15f) } ?: fallback
    }
}

@Composable
fun DetailsSection(
    title: String,
    onViewAllClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (onViewAllClick != null) {
                TextButton(onClick = onViewAllClick) {
                    Text(stringResource(R.string.view_all))
                }
            }
        }
        content()
    }
}

@Composable
fun SeeAllItem(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(28.dp))
            Text(
                text = stringResource(R.string.view_all),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsInfoItemPreview() {
    AppTheme {
        DetailsInfoItem(label = "Runtime", value = "120m")
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsSectionPreview() {
    AppTheme {
        DetailsSection(title = "Cast", onViewAllClick = {}) {
            Text("Sample Content")
        }
    }
}
