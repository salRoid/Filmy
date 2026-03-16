package tech.salroid.filmy.ui.movies.details.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.google.android.youtube.player.YouTubeStandalonePlayer
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.Youtube
import tech.salroid.filmy.ui.common.model.PaletteColors
import tech.salroid.filmy.ui.theme.AppTheme

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun TrailersSection(
    youtubeTrailers: List<Youtube>?,
    paletteColors: PaletteColors? = null,
    onPlusMoreClick: () -> Unit = {}
) {
    val context = LocalContext.current
    youtubeTrailers?.firstOrNull()?.let { trailer ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp),
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
                        .weight(8f)
                        .clickable {
                            val intent = Intent(
                                ACTION_VIEW,
                                "vnd.youtube:${trailer.source}".toUri()
                            )
                            if (intent.resolveActivity(context.packageManager) == null) {
                                context.startActivity(
                                    Intent(
                                        ACTION_VIEW,
                                        "http://www.youtube.com/watch?v=${trailer.source}".toUri()
                                    )
                                )
                            } else {
                                context.startActivity(intent)
                            }
                        }
                ) {
                    AsyncImage(
                        model = "https://img.youtube.com/vi/${trailer.source}/0.jpg",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(158.dp)
                            .padding(10.dp)
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
                        .weight(2f)
                        .fillMaxHeight()
                        .clickable { onPlusMoreClick() }
                        .padding(end = 12.dp),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTrailersSheet(
    title: String?,
    trailers: List<Youtube>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(R.drawable.ic_round_close_24),
                        contentDescription = "Close"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title ?: "",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trailers) { trailer ->
                    TrailerItem(trailer) {
                        trailer.source?.let { source ->
                            val intent = YouTubeStandalonePlayer.createVideoIntent(
                                context as? Activity,
                                BuildConfig.YOUTUBE_API_KEY,
                                source,
                                0,
                                true,
                                false
                            )
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrailerItem(trailer: Youtube, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = stringResource(R.string.trailer_img_url, trailer.source ?: ""),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
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
            Text(
                text = trailer.name ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
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
