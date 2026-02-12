package tech.salroid.filmy.ui.component

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.*
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.toReadableDate

data class PaletteColors(
    val vibrantRgb: Int? = null,
    val vibrantTitleTextColor: Int? = null,
    val vibrantBodyTextColor: Int? = null,
    val darkVibrantRgb: Int? = null,
    val darkVibrantBodyTextColor: Int? = null
)

@Composable
fun MovieDetailsHeader(
    movie: MovieDetails,
    paletteColors: PaletteColors? = null,
    onPaletteGenerated: (PaletteColors) -> Unit = {},
    onHeaderClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val backdropPrefix = stringResource(R.string.poster_prefix_500)

    val bannerUrl = remember(movie.backdropPath, movie.posterPath) {
        val path = if (movie.backdropPath != null && movie.backdropPath != "null") {
            movie.backdropPath
        } else {
            movie.posterPath
        }
        "$backdropPrefix$path"
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Backdrop
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(bannerUrl)
                    .allowHardware(false)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onSuccess = { state ->
                    val drawable = state.result.image.asDrawable(context.resources)
                    if (drawable is BitmapDrawable) {
                        val bitmap = drawable.bitmap
                        Palette.from(bitmap).generate { palette ->
                            val vibrant = palette?.vibrantSwatch ?: palette?.dominantSwatch
                            val darkVibrant = palette?.darkVibrantSwatch ?: palette?.vibrantSwatch
                            ?: palette?.dominantSwatch

                            onPaletteGenerated(
                                PaletteColors(
                                    vibrantRgb = vibrant?.rgb,
                                    vibrantTitleTextColor = vibrant?.titleTextColor,
                                    vibrantBodyTextColor = vibrant?.bodyTextColor,
                                    darkVibrantRgb = darkVibrant?.rgb,
                                    darkVibrantBodyTextColor = darkVibrant?.bodyTextColor
                                )
                            )
                        }
                    }
                }
            )
            // Backdrop Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        paletteColors?.darkVibrantRgb?.let { Color(it).copy(alpha = 0.7f) }
                            ?: Color.Black.copy(alpha = 0.5f)
                    )
            )
        }

        // Header Card Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 220.dp)
                .padding(horizontal = 8.dp)
                .clickable { onHeaderClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = paletteColors?.vibrantRgb?.let { Color(it) }
                    ?: MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Title and basic info positioned to the right of the poster
                Column(
                    modifier = Modifier
                        .padding(start = 156.dp, top = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = movie.title ?: "",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp
                        ),
                        color = paletteColors?.vibrantTitleTextColor?.let { Color(it) }
                            ?: Color.Unspecified
                    )

                    val genres = movie.genres.joinToString(" / ") { it.name ?: "" }
                    if (genres.isNotEmpty()) {
                        Text(
                            text = genres,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(if (paletteColors != null) 1f else 0.6f),
                            color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                                ?: Color.Unspecified
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val hours = movie.runtime?.div(60) ?: 0
                        val mins = movie.runtime?.rem(60) ?: 0
                        Text(
                            text = "${hours}h ${mins}m",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(if (paletteColors != null) 1f else 0.6f),
                            color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                                ?: Color.Unspecified
                        )

                        movie.releaseDate?.toReadableDate()?.let {
                            Text(
                                text = " • $it",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.alpha(if (paletteColors != null) 1f else 0.6f),
                                color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                                    ?: Color.Unspecified
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tagline
                if (!movie.tagline.isNullOrEmpty()) {
                    Text(
                        text = movie.tagline ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .alpha(if (paletteColors != null) 1f else 0.8f),
                        color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                            ?: Color.Unspecified
                    )
                }

                // Overview
                if (!movie.overview.isNullOrEmpty()) {
                    Text(
                        text = movie.overview ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 8.dp)
                            .alpha(if (paletteColors != null) 1f else 0.7f),
                        color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                            ?: Color.Unspecified
                    )
                }
            }
        }

        // Poster Container
        Card(
            modifier = Modifier
                .padding(start = 24.dp, top = 145.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            AsyncImage(
                model = stringResource(R.string.movie_poster_url, movie.posterPath ?: ""),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp, 180.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun TvDetailsHeader(
    show: TvDetails,
    paletteColors: PaletteColors? = null,
    onPaletteGenerated: (PaletteColors) -> Unit = {},
    onHeaderClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val backdropPrefix = stringResource(R.string.poster_prefix_500)

    val bannerUrl = remember(show.backdropPath, show.posterPath) {
        val path = if (show.backdropPath != null && show.backdropPath != "null") {
            show.backdropPath
        } else {
            show.posterPath
        }
        "$backdropPrefix$path"
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Backdrop
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(bannerUrl)
                    .allowHardware(false)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onSuccess = { state ->
                    val drawable = state.result.image.asDrawable(context.resources)
                    if (drawable is BitmapDrawable) {
                        val bitmap = drawable.bitmap
                        Palette.from(bitmap).generate { palette ->
                            val vibrant = palette?.vibrantSwatch ?: palette?.dominantSwatch
                            val darkVibrant = palette?.darkVibrantSwatch ?: palette?.vibrantSwatch
                                ?: palette?.dominantSwatch

                            onPaletteGenerated(
                                PaletteColors(
                                    vibrantRgb = vibrant?.rgb,
                                    vibrantTitleTextColor = vibrant?.titleTextColor,
                                    vibrantBodyTextColor = vibrant?.bodyTextColor,
                                    darkVibrantRgb = darkVibrant?.rgb,
                                    darkVibrantBodyTextColor = darkVibrant?.bodyTextColor
                                )
                            )
                        }
                    }
                }
            )
            // Backdrop Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        paletteColors?.darkVibrantRgb?.let { Color(it).copy(alpha = 0.7f) }
                            ?: Color.Black.copy(alpha = 0.5f)
                    )
            )
        }

        // Header Card Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 220.dp)
                .padding(horizontal = 8.dp)
                .clickable { onHeaderClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = paletteColors?.vibrantRgb?.let { Color(it) }
                    ?: MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Title and basic info positioned to the right of the poster
                Column(
                    modifier = Modifier
                        .padding(start = 156.dp, top = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = show.name ?: "",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp
                        ),
                        color = paletteColors?.vibrantTitleTextColor?.let { Color(it) }
                            ?: Color.Unspecified
                    )

                    val genres = show.genres.joinToString(" / ") { it.name ?: "" }
                    if (genres.isNotEmpty()) {
                        Text(
                            text = genres,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(if (paletteColors != null) 1f else 0.6f),
                            color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                                ?: Color.Unspecified
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val runtime = show.episodeRunTime.firstOrNull() ?: 0
                        Text(
                            text = "${runtime}m",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(if (paletteColors != null) 1f else 0.6f),
                            color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                                ?: Color.Unspecified
                        )

                        show.firstAirDate?.toReadableDate()?.let {
                            Text(
                                text = " • $it",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.alpha(if (paletteColors != null) 1f else 0.6f),
                                color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                                    ?: Color.Unspecified
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tagline
                if (!show.tagline.isNullOrEmpty()) {
                    Text(
                        text = show.tagline ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .alpha(if (paletteColors != null) 1f else 0.8f),
                        color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                            ?: Color.Unspecified
                    )
                }

                // Overview
                if (!show.overview.isNullOrEmpty()) {
                    Text(
                        text = show.overview ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 4,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 8.dp)
                            .alpha(if (paletteColors != null) 1f else 0.7f),
                        color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                            ?: Color.Unspecified
                    )
                }
            }
        }

        // Poster Container
        Card(
            modifier = Modifier
                .padding(start = 24.dp, top = 145.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            AsyncImage(
                model = stringResource(R.string.movie_poster_url, show.posterPath ?: ""),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp, 180.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun DetailsHeader(
    title: String,
    tagline: String?,
    posterPath: String?,
    backdropPath: String?,
    paletteColors: PaletteColors? = null,
    onPaletteGenerated: (PaletteColors) -> Unit = {},
    onHeaderClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val backdropPrefix = stringResource(R.string.poster_prefix_500)

    val bannerUrl = remember(backdropPath, posterPath) {
        val path = if (backdropPath != null && backdropPath != "null") {
            backdropPath
        } else {
            posterPath
        }
        "$backdropPrefix$path"
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Backdrop
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(bannerUrl)
                    .allowHardware(false)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onSuccess = { state ->
                    val drawable = state.result.image.asDrawable(context.resources)
                    if (drawable is BitmapDrawable) {
                        val bitmap = drawable.bitmap
                        Palette.from(bitmap).generate { palette ->
                            val vibrant = palette?.vibrantSwatch ?: palette?.dominantSwatch
                            val darkVibrant = palette?.darkVibrantSwatch ?: palette?.vibrantSwatch
                            ?: palette?.dominantSwatch

                            onPaletteGenerated(
                                PaletteColors(
                                    vibrantRgb = vibrant?.rgb,
                                    vibrantTitleTextColor = vibrant?.titleTextColor,
                                    vibrantBodyTextColor = vibrant?.bodyTextColor,
                                    darkVibrantRgb = darkVibrant?.rgb,
                                    darkVibrantBodyTextColor = darkVibrant?.bodyTextColor
                                )
                            )
                        }
                    }
                }
            )
            // Backdrop Scrim
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        paletteColors?.darkVibrantRgb?.let { Color(it).copy(alpha = 0.7f) }
                            ?: Color.Black.copy(alpha = 0.5f)
                    )
            )
        }

        // Header Card Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 220.dp)
                .padding(horizontal = 8.dp)
                .clickable { onHeaderClick() },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = paletteColors?.vibrantRgb?.let { Color(it) }
                    ?: MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                // Title and basic info positioned to the right of the poster
                Column(
                    modifier = Modifier
                        .padding(start = 156.dp, top = 16.dp, end = 16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp
                        ),
                        color = paletteColors?.vibrantTitleTextColor?.let { Color(it) }
                            ?: Color.Unspecified
                    )

                    if (!tagline.isNullOrEmpty()) {
                        Text(
                            text = tagline,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(if (paletteColors != null) 1f else 0.6f),
                            color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                                ?: Color.Unspecified
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Poster Container
        Card(
            modifier = Modifier
                .padding(start = 24.dp, top = 160.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            AsyncImage(
                model = stringResource(R.string.movie_poster_url, posterPath ?: ""),
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp, 180.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun DetailsInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.alpha(0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
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
fun RatingsSection(voteAverage: Double?, voteCount: Long?) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth(),
        border = BorderStroke(0.dp, Color.Transparent),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            voteAverage?.let {
                CircularProgressIndicator(
                    progress = { it.toFloat() / 10f },
                    modifier = Modifier.size(24.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                )

                Spacer(modifier = Modifier.width(16.dp))

                Image(
                    painter = painterResource(R.drawable.tmdb_logo),
                    contentDescription = "TMDB",
                    modifier = Modifier.size(42.dp, 20.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "${(it * 10).toInt()}% User Score",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "by $voteCount Users",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.alpha(0.6f)
                    )
                }
            }
        }
    }
}

@SuppressLint("QueryPermissionsNeeded")
@Composable
fun TrailersSection(
    youtubeTrailers: List<tech.salroid.filmy.data.local.model.Youtube>?,
    paletteColors: PaletteColors? = null
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
                        .clickable {
                            val intent = Intent(
                                ACTION_VIEW,
                                "https://www.youtube.com/results?search_query=${trailer.name}".toUri()
                            )
                            context.startActivity(intent)
                        }
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

@Composable
fun WatchProvidersSection(watchProviders: WatchProviderResponse?) {
    watchProviders?.let { providers ->
        val stream = providers.results?.IN?.flatrate?.firstOrNull()
        val buy = providers.results?.IN?.buy?.firstOrNull()
        val rent = providers.results?.IN?.rent?.firstOrNull()

        val logoPath = stream?.logoPath ?: buy?.logoPath ?: rent?.logoPath
        val providerName = stream?.providerName ?: buy?.providerName ?: rent?.providerName

        if (logoPath != null && providerName != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = stringResource(R.string.member_profile_url, logoPath),
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.now_streaming),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.watch_now),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemberItem(
    name: String?,
    profilePath: String?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = stringResource(R.string.member_profile_url, profilePath ?: ""),
            contentDescription = null,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.default_avatar),
            error = painterResource(R.drawable.default_avatar)
        )
        Text(
            text = name ?: "",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun CastSection(
    cast: List<Cast>?,
    id: Int,
    title: String,
    isTv: Boolean,
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit
) {
    if (cast?.isNotEmpty() == true) {
        DetailsSection(
            title = stringResource(R.string.cast),
            onViewAllClick = { onViewAllCastClick(id, isTv, title) }
        ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(cast.take(5)) { castMember ->
                    MemberItem(
                        name = castMember.name,
                        profilePath = castMember.profilePath,
                        onClick = { onMemberClick(castMember.id ?: 0, isTv) }
                    )
                }
            }
        }
    }
}

@Composable
fun CrewSection(
    crew: List<Crew>?,
    id: Int,
    title: String,
    isTv: Boolean,
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit
) {
    if (crew?.isNotEmpty() == true) {
        DetailsSection(
            title = stringResource(R.string.crew),
            onViewAllClick = { onViewAllCastClick(id, isTv, title) }
        ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(crew.take(5)) { crewMember ->
                    MemberItem(
                        name = crewMember.name,
                        profilePath = crewMember.profilePath,
                        onClick = { onMemberClick(crewMember.id ?: 0, isTv) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewsSection(reviews: ReviewResponse?, onReviewClick: (String, String) -> Unit) {
    if (reviews?.results?.isNotEmpty() == true) {
        DetailsSection(title = "Reviews") {
            reviews.results.take(2).forEach { review ->
                ReviewItem(review) {
                    onReviewClick(review.author ?: "", review.content ?: "")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MediaSuggestionsSection(
    title: String,
    response: SimilarMoviesResponse?,
    onMediaClick: (Int) -> Unit
) {
    if (response?.results?.isNotEmpty() == true) {
        DetailsSection(title = title) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(response.results) { media ->
                    SuggestionItem(media.title, media.posterPath) {
                        onMediaClick(media.id ?: 0)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review, onClick: () -> Unit = {}) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = review.authorDetails?.getAvatarUrl(LocalContext.current),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(R.drawable.default_avatar),
                    error = painterResource(R.drawable.default_avatar)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = review.author ?: "", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = review.createdAt?.toReadableDate() ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.alpha(0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.content ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SuggestionItem(title: String?, posterPath: String?, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = stringResource(R.string.movie_poster_url, posterPath ?: ""),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp, 145.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = title ?: "",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MovieDetailsHeaderPreview() {
    val sampleMovie = MovieDetails(
        id = 1,
        title = "Inception",
        backdropPath = "/8Z99vYmda99uSHI6fSToMvSztpZ.jpg",
        posterPath = "/edv5bs1pS9v796LpT2M0sYhC76B.jpg",
        genres = arrayListOf(Genre(id = 1, name = "Action"), Genre(id = 2, name = "Sci-Fi")),
        runtime = 148,
        releaseDate = "2010-07-16",
        tagline = "Your mind is the scene of the crime.",
        overview = "Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets is offered a chance to regain his old life as payment for a task considered to be impossible: \"inception\", the implantation of another person's idea into a target's subconscious."
    )
    AppTheme {
        MovieDetailsHeader(movie = sampleMovie)
    }
}
