package tech.salroid.filmy.ui.movies.details.components

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.*
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun RatingsSection(
    ratings: RatingsUiModel?,
    paletteColors: PaletteColors? = null
) {
    if (ratings == null || ratings.ratings.isEmpty()) return

    val context = LocalContext.current
    val openUrl = { url: String ->
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, url.toUri())
    }

    val ratingList = ratings.ratings

    Column {
        Text(
            text = "Ratings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        when (ratingList.size) {
            1 -> {
                // Single rating: Full width
                RatingCard(
                    rating = ratingList[0],
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    paletteColors = paletteColors,
                    onClick = { ratingList[0].url?.let { openUrl(it) } }
                )
            }

            2 -> {
                // Two ratings: Half width each
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RatingCard(
                        rating = ratingList[0],
                        modifier = Modifier.weight(1f),
                        paletteColors = paletteColors,
                        onClick = { ratingList[0].url?.let { openUrl(it) } }
                    )
                    RatingCard(
                        rating = ratingList[1],
                        modifier = Modifier.weight(1f),
                        paletteColors = paletteColors,
                        onClick = { ratingList[1].url?.let { openUrl(it) } }
                    )
                }
            }

            else -> {
                // More than two: Default horizontal list
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(ratingList) { rating ->
                        RatingCard(
                            rating = rating,
                            modifier = Modifier
                                .width(IntrinsicSize.Max)
                                .widthIn(min = 130.dp),
                            paletteColors = paletteColors,
                            onClick = { rating.url?.let { openUrl(it) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RatingCard(
    rating: RatingSourceUiModel,
    modifier: Modifier = Modifier,
    paletteColors: PaletteColors? = null,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val defaultCardColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val backgroundColor = remember(paletteColors, isDark, defaultCardColor) {
        val colorInt = if (isDark) {
            paletteColors?.darkMutedRgb ?: paletteColors?.darkVibrantRgb
        } else {
            paletteColors?.lightMutedRgb ?: paletteColors?.lightVibrantRgb
        }
        colorInt?.let { Color(it).copy(alpha = 0.15f) }
            ?: defaultCardColor
    }

    Surface(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = rating.url != null, onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 0.5.dp,
            color = backgroundColor.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (rating.source) {
                RatingSource.IMDB -> R.drawable.imdb
                RatingSource.ROTTEN_TOMATOES -> R.drawable.rotten
                RatingSource.TMDB -> R.drawable.tmdb_logo
                else -> R.drawable.ic_stars
            }

            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = rating.value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = when (rating.source) {
                        RatingSource.IMDB -> "IMDb"
                        RatingSource.ROTTEN_TOMATOES -> "Rotten Tomatoes"
                        RatingSource.TMDB -> "TMDB"
                        RatingSource.METACRITIC -> "Metacritic"
                        else -> "Rating"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    lineHeight = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (rating.url != null) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.0f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(12.dp)
                            .graphicsLayer {
                                rotationZ = -45f
                            }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingsSectionPreview() {
    val sampleRatings = RatingsUiModel(
        ratings = listOf(
            RatingSourceUiModel(RatingSource.IMDB, "5.3/10", "https://imdb.com"),
            RatingSourceUiModel(RatingSource.ROTTEN_TOMATOES, "12%", "https://rottentomatoes.com")
        )
    )
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RatingsSection(ratings = sampleRatings)
        }
    }
}
