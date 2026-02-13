package tech.salroid.filmy.ui.component

import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.Genre
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.toReadableDate

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
                        style = MaterialTheme.typography.bodySmall,
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

@Preview(showBackground = true)
@Composable
fun TvDetailsHeaderPreview() {
    val sampleShow = TvDetails(
        id = 1,
        name = "Breaking Bad",
        backdropPath = "/8Z99vYmda99uSHI6fSToMvSztpZ.jpg",
        posterPath = "/edv5bs1pS9v796LpT2M0sYhC76B.jpg",
        genres = arrayListOf(Genre(id = 1, name = "Drama"), Genre(id = 2, name = "Crime")),
        episodeRunTime = arrayListOf(45),
        firstAirDate = "2008-01-20",
        tagline = "All Hail the King",
        overview = "A high school chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine in order to secure his family's future."
    )
    AppTheme {
        TvDetailsHeader(show = sampleShow)
    }
}
