package tech.salroid.filmy.ui.common.components

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.DetailsActions
import tech.salroid.filmy.ui.common.model.MediaDetailsUiState
import tech.salroid.filmy.ui.common.model.PaletteColors
import tech.salroid.filmy.ui.movies.details.components.*
import tech.salroid.filmy.utility.themeSystemBars

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsContent(
    state: MediaDetailsUiState,
    actions: DetailsActions,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scrollState = rememberScrollState()

    val scrollThreshold = with(LocalDensity.current) { 200.dp.toPx() }
    val visibilityModifier by remember {
        derivedStateOf {
            (1f - (scrollState.value / scrollThreshold)).coerceIn(0f, 1f)
        }
    }

    var paletteColors by remember(state.mediaId) { mutableStateOf<PaletteColors?>(null) }
    var showFullRead by remember { mutableStateOf(false) }
    var showAllTrailers by remember { mutableStateOf(false) }
    var fullReadContent by remember { mutableStateOf(Pair("", "")) }

    val iconColor =
        if (visibilityModifier > 0.5f) Color.White else MaterialTheme.colorScheme.onSurface

    val isDark = isSystemInDarkTheme()
    val defaultBackground = MaterialTheme.colorScheme.background
    val scaffoldBackgroundColor = remember(paletteColors, isDark) {
        val currentPaletteColors = paletteColors ?: return@remember defaultBackground

        val colorInt = if (isDark) {
            currentPaletteColors.darkMutedRgb ?: currentPaletteColors.darkVibrantRgb
        } else {
            currentPaletteColors.lightMutedRgb ?: currentPaletteColors.lightVibrantRgb
        }

        colorInt?.let { Color(it).copy(alpha = 0.15f).compositeOver(defaultBackground) }
            ?: defaultBackground
    }

    val containerColor = scaffoldBackgroundColor.copy(alpha = 1f - visibilityModifier)

    LaunchedEffect(visibilityModifier) {
        activity?.themeSystemBars(
            lightStatusBar = visibilityModifier <= 0.5f,
            isFullScreen = true,
            transparentStatus = true
        )
    }

    if (showFullRead) {
        ModalBottomSheet(
            onDismissRequest = { showFullRead = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = fullReadContent.first,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = fullReadContent.second,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showAllTrailers) {
        AllTrailersSheet(
            title = state.title,
            trailers = state.youtubeTrailers ?: emptyList(),
            onDismiss = { showAllTrailers = false }
        )
    }

    val imdbPrefix = stringResource(R.string.imdb_link_prefix)

    Scaffold(
        containerColor = scaffoldBackgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = actions.onBackNavigation) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = iconColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = actions.onFavoriteToggle) {
                        Icon(
                            painter = if (state.isFavorite) painterResource(R.drawable.ic_round_favorite_24) else painterResource(
                                R.drawable.ic_round_favorite_border_24
                            ),
                            contentDescription = "Favorite",
                            tint = if (state.isFavorite) Color.Red else iconColor
                        )
                    }
                    IconButton(onClick = actions.onWatchlistToggle) {
                        Icon(
                            painter = if (state.isWatchlist) painterResource(R.drawable.ic_round_bookmark_added_24) else painterResource(
                                R.drawable.ic_round_bookmark_add_24
                            ),
                            contentDescription = "Watchlist",
                            tint = iconColor
                        )
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            val prefix = if (!state.isTvShow) imdbPrefix else ""
                            val link =
                                if (!state.isTvShow && state.imdbId != null) "\n$prefix${state.imdbId}" else ""
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "*${state.title}*\n${state.tagline}$link\n"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share with"))
                    }) {
                        Icon(
                            painterResource(R.drawable.twotone_share_24),
                            contentDescription = "Share",
                            tint = iconColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = containerColor,
                    titleContentColor = iconColor,
                    navigationIconContentColor = iconColor,
                    actionIconContentColor = iconColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            DetailsHeader(
                state = state,
                paletteColors = paletteColors,
                onPaletteGenerated = { paletteColors = it },
                onHeaderClick = {
                    fullReadContent = Pair(state.title, state.overview)
                    showFullRead = true
                }
            )

            Column(modifier = Modifier.padding(16.dp)) {
                WatchProvidersSection(state.watchProviders)

                Text(
                    text = stringResource(R.string.ratings),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                RatingsSection(state.voteAverage, state.voteCount)

                TrailersSection(
                    youtubeTrailers = state.youtubeTrailers,
                    paletteColors = paletteColors,
                    onPlusMoreClick = { showAllTrailers = true }
                )

                CastSection(
                    state.castAndCrew?.cast,
                    state.mediaId,
                    state.title,
                    state.isTvShow,
                    actions.onViewAllCastClick,
                    actions.onMemberClick
                )
                CrewSection(
                    state.castAndCrew?.crew,
                    state.mediaId,
                    state.title,
                    state.isTvShow,
                    actions.onViewAllCastClick,
                    actions.onMemberClick
                )
                ReviewsSection(state.reviews) { reviewTitle, content ->
                    fullReadContent = Pair(reviewTitle, content)
                    showFullRead = true
                }
                MediaSuggestionsSection(
                    title = if (state.isTvShow) "Similar Shows" else "Similar",
                    response = state.similarMedia,
                    onMediaClick = actions.onMediaClick
                )
                MediaSuggestionsSection(
                    title = if (state.isTvShow) "Recommendations" else "Recommended",
                    response = state.recommendations,
                    onMediaClick = actions.onMediaClick
                )
            }
        }
    }
}