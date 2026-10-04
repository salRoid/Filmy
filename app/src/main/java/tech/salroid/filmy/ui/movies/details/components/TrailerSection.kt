package tech.salroid.filmy.ui.movies.details.components

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.Youtube
import tech.salroid.filmy.ui.common.model.PaletteColors
import tech.salroid.filmy.ui.theme.AppTheme

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun TrailersSection(
    youtubeTrailers: List<Youtube>?,
    paletteColors: PaletteColors? = null,
    onTrailerClick: (String) -> Unit = {},
    onPlusMoreClick: () -> Unit = {}
) {
    youtubeTrailers?.firstOrNull()?.let { trailer ->
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = paletteColors?.darkVibrantRgb?.let { Color(it) }
                    ?: MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Box(
                    modifier = Modifier
                        .weight(7.5f)
                        .clickable {
                            trailer.source?.let { onTrailerClick(it) }
                        }
                ) {
                    AsyncImage(
                        model = "https://img.youtube.com/vi/${trailer.source}/0.jpg",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(154.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_play_circle_filled_white_48dp),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(2.5f)
                        .fillMaxHeight()
                        .clickable { onPlusMoreClick() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.twotone_video_library_24),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = paletteColors?.darkVibrantBodyTextColor?.let { Color(it) }
                            ?: LocalContentColor.current
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.plus_more).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        color = paletteColors?.darkVibrantBodyTextColor?.let { Color(it) }
                            ?: Color.Unspecified
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrailersSectionPreview() {
    val sampleTrailers = listOf(
        Youtube(name = "Official Trailer", source = "8Z99vYmda99uSHI6fSToMvSztpZ"),
        Youtube(name = "Teaser Trailer", source = "8Z99vYmda99uSHI6fSToMvSztpZ")
    )
    AppTheme {
        TrailersSection(youtubeTrailers = sampleTrailers)
    }
}
