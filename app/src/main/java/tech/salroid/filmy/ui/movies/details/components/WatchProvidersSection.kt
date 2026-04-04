package tech.salroid.filmy.ui.movies.details.components

import android.content.Intent
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.PaletteColors
import tech.salroid.filmy.ui.common.model.ProviderUiModel
import tech.salroid.filmy.ui.common.model.WatchProvidersUiModel
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun WatchProvidersSection(
    watchProviders: WatchProvidersUiModel,
    paletteColors: PaletteColors? = null
) {
    val context = LocalContext.current
    val link = watchProviders.link
    val providers = watchProviders.providers

    if (providers.isNotEmpty()) {
        val onProviderClick = {
            link?.let {
                val intent = Intent(Intent.ACTION_VIEW, it.toUri())
                context.startActivity(intent)
            }
        }

        Column(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = stringResource(R.string.where_to_watch),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            when (providers.size) {
                1 -> {
                    ProviderCard(
                        provider = providers[0],
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        paletteColors = paletteColors,
                        onClick = { onProviderClick() }
                    )
                }

                2 -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProviderCard(
                            provider = providers[0],
                            modifier = Modifier.weight(1f),
                            paletteColors = paletteColors,
                            onClick = { onProviderClick() }
                        )
                        ProviderCard(
                            provider = providers[1],
                            modifier = Modifier.weight(1f),
                            paletteColors = paletteColors,
                            onClick = { onProviderClick() }
                        )
                    }
                }

                else -> {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        items(providers) { provider ->
                            ProviderCard(
                                provider = provider,
                                modifier = Modifier
                                    .width(IntrinsicSize.Max)
                                    .widthIn(min = 130.dp),
                                paletteColors = paletteColors,
                                onClick = { onProviderClick() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderCard(
    provider: ProviderUiModel,
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
            .clickable(onClick = onClick),
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
            AsyncImage(
                model = stringResource(R.string.member_profile_url, provider.logoPath),
                contentDescription = provider.name,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                modifier = Modifier.weight(1f),
                text = provider.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 12.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                lineHeight = 14.sp,
                overflow = TextOverflow.Ellipsis
            )

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

@Preview(showBackground = true)
@Composable
fun WatchProvidersSectionPreview() {
    val sampleProviders = WatchProvidersUiModel(
        link = "https://www.themoviedb.org",
        providers = listOf(
            ProviderUiModel(1, "Netflix", "/t2Y9jYvMvIDpS0T3H4pYt6pS6pS.jpg"),
            ProviderUiModel(2, "Amazon Prime Video", "/t2Y9jYvMvIDpS0T3H4pYt6pS6pS.jpg")
        )
    )
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            WatchProvidersSection(watchProviders = sampleProviders)
        }
    }
}
