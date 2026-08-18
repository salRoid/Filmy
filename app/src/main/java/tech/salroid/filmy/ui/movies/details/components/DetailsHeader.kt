package tech.salroid.filmy.ui.movies.details.components

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import coil3.asDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.MediaDetailsUiState
import tech.salroid.filmy.ui.common.model.PaletteColors
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun DetailsHeader(
    state: MediaDetailsUiState,
    paletteColors: PaletteColors? = null,
    onPaletteGenerated: (PaletteColors) -> Unit = {},
    onHeaderClick: () -> Unit = {}
) {
    val density = LocalDensity.current
    val backdropPrefix = stringResource(R.string.poster_prefix_500)
    var posterTop by remember { mutableStateOf(135.dp) }

    var isPosterLoaded by remember { mutableStateOf(false) }
    val tiltAnim = remember { Animatable(0f) }
    val shineAnim = remember { Animatable(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val playAnimation = {
        if (!isAnimating) {
            isAnimating = true
            coroutineScope.launch {
                shineAnim.snapTo(0f)
                val job1 = launch {
                    tiltAnim.animateTo(
                        targetValue = 15f,
                        animationSpec = tween(300, easing = FastOutLinearInEasing)
                    )
                    tiltAnim.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(500, easing = LinearOutSlowInEasing)
                    )
                }
                val job2 = launch {
                    shineAnim.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(800, easing = FastOutSlowInEasing)
                    )
                }
                job1.join()
                job2.join()
                isAnimating = false
            }
        }
    }

    LaunchedEffect(isPosterLoaded) {
        if (isPosterLoaded) {
            delay(150)
            playAnimation()
        }
    }

    val mainBackdropPath = remember(state.backdropPath, state.posterPath) {
        if (state.backdropPath != null && state.backdropPath != "null") {
            state.backdropPath
        } else {
            state.posterPath
        }
    }

    val bannerUrls = remember(mainBackdropPath, state.backdropImages) {
        val extra = state.backdropImages.orEmpty().filter { it != mainBackdropPath }
        (listOfNotNull(mainBackdropPath) + extra).distinct().map { "$backdropPrefix$it" }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        HeaderBackdrop(
            bannerUrls = bannerUrls,
            paletteColors = paletteColors,
            onPaletteGenerated = onPaletteGenerated
        )

        HeaderInfoCard(
            state = state,
            paletteColors = paletteColors,
            onHeaderClick = onHeaderClick,
            onBottomPositionCalculated = { bottom ->
                posterTop = 220.dp + with(density) { bottom.toDp() } - 180.dp
            }
        )

        HeaderPoster(
            posterUrl = stringResource(R.string.movie_poster_url, state.posterPath ?: ""),
            posterTop = posterTop,
            tiltAnimValue = tiltAnim.value,
            shineAnimValue = shineAnim.value,
            onPosterLoaded = { isPosterLoaded = true },
            onClick = playAnimation
        )
    }
}

private const val BACKDROP_ROTATION_INTERVAL_MS = 6000L
private const val BACKDROP_CROSSFADE_DURATION_MS = 1200

@Composable
fun HeaderBackdrop(
    bannerUrls: List<String>,
    paletteColors: PaletteColors?,
    onPaletteGenerated: (PaletteColors) -> Unit
) {
    val context = LocalContext.current
    var currentIndex by remember { mutableIntStateOf(0) }
    var hasGeneratedPalette by remember { mutableStateOf(false) }

    // Cycle through the fetched backdrops once there's more than one to show.
    LaunchedEffect(bannerUrls) {
        if (bannerUrls.size <= 1) return@LaunchedEffect
        while (true) {
            delay(BACKDROP_ROTATION_INTERVAL_MS)
            currentIndex = (currentIndex + 1) % bannerUrls.size
        }
    }

    val currentUrl = bannerUrls.getOrNull(currentIndex) ?: bannerUrls.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Crossfade(
            targetState = currentUrl,
            animationSpec = tween(BACKDROP_CROSSFADE_DURATION_MS),
            label = "backdrop_crossfade"
        ) { url ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onSuccess = { imageState ->
                    // Only the very first backdrop drives the screen's color theme —
                    // later rotations shouldn't shift the rest of the UI's colors.
                    if (hasGeneratedPalette) return@AsyncImage
                    val drawable = imageState.result.image.asDrawable(context.resources)
                    if (drawable is BitmapDrawable) {
                        hasGeneratedPalette = true
                        val bitmap = drawable.bitmap
                        Palette.from(bitmap).generate { palette ->
                            val vibrant = palette?.vibrantSwatch ?: palette?.dominantSwatch
                            val darkVibrant = palette?.darkVibrantSwatch ?: palette?.vibrantSwatch
                            ?: palette?.dominantSwatch
                            val lightVibrant = palette?.lightVibrantSwatch
                            val lightMuted = palette?.lightMutedSwatch
                            val darkMuted = palette?.darkMutedSwatch
                            val muted = palette?.mutedSwatch

                            onPaletteGenerated(
                                PaletteColors(
                                    vibrantRgb = vibrant?.rgb,
                                    vibrantTitleTextColor = vibrant?.titleTextColor,
                                    vibrantBodyTextColor = vibrant?.bodyTextColor,
                                    darkVibrantRgb = darkVibrant?.rgb,
                                    darkVibrantBodyTextColor = darkVibrant?.bodyTextColor,
                                    lightVibrantRgb = lightVibrant?.rgb,
                                    lightMutedRgb = lightMuted?.rgb,
                                    darkMutedRgb = darkMuted?.rgb,
                                    mutedRgb = muted?.rgb
                                )
                            )
                        }
                    }
                }
            )
        }
        // Backdrop Scrim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    paletteColors?.darkVibrantRgb?.let { Color(it).copy(alpha = 0.3f) }
                        ?: Color.Black.copy(alpha = 0.3f)
                )
        )
    }
}

@Composable
fun HeaderInfoCard(
    state: MediaDetailsUiState,
    paletteColors: PaletteColors?,
    onHeaderClick: () -> Unit,
    onBottomPositionCalculated: (Float) -> Unit
) {
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
                .padding(bottom = 16.dp)
        ) {
            // Title and basic info positioned to the right of the poster
            Column(
                modifier = Modifier
                    .padding(start = 156.dp, top = 16.dp, end = 16.dp)
                    .onGloballyPositioned { coords ->
                        val bottom = coords.positionInParent().y + coords.size.height
                        onBottomPositionCalculated(bottom)
                    }
            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp
                    ),
                    color = paletteColors?.vibrantTitleTextColor?.let { Color(it) }
                        ?: Color.Unspecified
                )

                if (state.genres.isNotEmpty()) {
                    Text(
                        text = state.genres,
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
                    if (!state.certification.isNullOrEmpty()) {
                        val badgeContentColor = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                            ?: MaterialTheme.colorScheme.onSecondaryContainer
                        Surface(
                            color = badgeContentColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = state.certification,
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeContentColor,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    if (state.runtimeText.isNotEmpty()) {
                        Text(
                            text = state.runtimeText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.alpha(if (paletteColors != null) 1f else 0.6f),
                            color = paletteColors?.vibrantBodyTextColor?.let { Color(it) }
                                ?: Color.Unspecified
                        )
                    }

                    if (state.releaseDateText.isNotEmpty()) {
                        Text(
                            text = state.releaseDateText,
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
            if (state.tagline.isNotEmpty()) {
                Text(
                    text = state.tagline,
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
            if (state.overview.isNotEmpty()) {
                Text(
                    text = state.overview,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
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
}

@Composable
fun HeaderPoster(
    posterUrl: String,
    posterTop: Dp,
    tiltAnimValue: Float,
    shineAnimValue: Float,
    onPosterLoaded: () -> Unit,
    onClick: () -> Unit
) {
    val density = LocalDensity.current
    Card(
        modifier = Modifier
            .padding(start = 24.dp, top = posterTop)
            .graphicsLayer {
                cameraDistance = 12f * density.density
                rotationY = tiltAnimValue
                rotationX = -tiltAnimValue * 0.2f
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.size(120.dp, 180.dp)) {
            AsyncImage(
                model = posterUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onSuccess = { onPosterLoaded() },
                onError = { onPosterLoaded() }
            )

            if (shineAnimValue in 0.01f..0.99f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val progress = shineAnimValue
                    val w = size.width
                    val h = size.height

                    val offset = (progress * 2f) - 0.5f
                    val startX = w * offset
                    val startY = h * offset

                    val brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        start = Offset(startX, startY),
                        end = Offset(startX + w * 0.5f, startY + h * 0.5f)
                    )
                    drawRect(brush = brush)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsHeaderPreview() {
    AppTheme {
        DetailsHeader(
            state = MediaDetailsUiState(
                mediaId = 1,
                title = "Inception",
                overview = "Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets is offered a chance to regain his old life as payment for a task considered to be impossible: \"inception\", the implantation of another person's idea into a target's subconscious.",
                tagline = "Your mind is the scene of the crime.",
                backdropPath = "/8Z99vYmda99uSHI6fSToMvSztpZ.jpg",
                posterPath = "/edv5bs1pS9v796LpT2M0sYhC76B.jpg",
                genres = "Action / Sci-Fi",
                runtimeText = "2h 28m",
                releaseDateText = " • 16 Jul 2010",
                youtubeTrailers = null,
                isWatched = false,
                isWatchlist = false,
                isTvShow = false
            )
        )
    }
}
