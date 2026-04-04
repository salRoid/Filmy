package tech.salroid.filmy.ui.common.components

import android.app.Activity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.DetailsActions
import tech.salroid.filmy.ui.common.model.MediaDetailsUiState
import tech.salroid.filmy.ui.common.model.PaletteColors
import tech.salroid.filmy.ui.movies.details.components.*
import tech.salroid.filmy.utility.themeSystemBars

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

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(visibilityModifier, isDark) {
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
            activity?.themeSystemBars(
                lightStatusBar = visibilityModifier <= 0.5f,
                isFullScreen = true,
                transparentStatus = true
            )
        }
    }

    LifecycleResumeEffect(Unit) {
        activity?.themeSystemBars(
            lightStatusBar = visibilityModifier <= 0.5f,
            isFullScreen = true,
            transparentStatus = true
        )
        onPauseOrDispose {
            activity?.themeSystemBars(
                lightStatusBar = !isDark,
                isFullScreen = true,
                transparentStatus = true
            )
        }
    }

    if (showFullRead) {
        FullReadSheet(
            title = fullReadContent.first,
            content = fullReadContent.second,
            onDismiss = { showFullRead = false }
        )
    }

    if (showAllTrailers) {
        AllTrailersSheet(
            title = state.title,
            trailers = state.youtubeTrailers ?: emptyList(),
            onTrailerClick = actions.onTrailerClick,
            onDismiss = { showAllTrailers = false }
        )
    }

    DetailsScaffold(
        state = state,
        actions = actions,
        scaffoldBackgroundColor = scaffoldBackgroundColor,
        containerColor = containerColor,
        iconColor = iconColor,
        scrollState = scrollState,
        paletteColors = paletteColors,
        onPaletteGenerated = { paletteColors = it },
        onHeaderClick = {
            fullReadContent = Pair(state.title, state.overview)
            showFullRead = true
        },
        onReviewClick = { title, content ->
            fullReadContent = Pair(title, content)
            showFullRead = true
        },
        onAllTrailersClick = { showAllTrailers = true },
        modifier = modifier
    )
}

@Composable
fun DetailsScaffold(
    state: MediaDetailsUiState,
    actions: DetailsActions,
    scaffoldBackgroundColor: Color,
    containerColor: Color,
    iconColor: Color,
    scrollState: ScrollState,
    paletteColors: PaletteColors?,
    onPaletteGenerated: (PaletteColors) -> Unit,
    onHeaderClick: () -> Unit,
    onReviewClick: (String, String) -> Unit,
    onAllTrailersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = scaffoldBackgroundColor,
        topBar = {
            DetailsToolbar(
                actions = actions,
                iconColor = iconColor,
                containerColor = containerColor
            )
        }
    ) { paddingValues ->
        DetailsMainContent(
            state = state,
            actions = actions,
            scrollState = scrollState,
            paletteColors = paletteColors,
            onPaletteGenerated = onPaletteGenerated,
            onHeaderClick = onHeaderClick,
            onReviewClick = onReviewClick,
            onAllTrailersClick = onAllTrailersClick,
            paddingValues = paddingValues,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsToolbar(
    actions: DetailsActions,
    iconColor: Color,
    containerColor: Color
) {
    val isTransparent = containerColor.alpha < 0.5f
    val buttonBackgroundColor =
        if (isTransparent) Color.Black.copy(alpha = 0.35f) else Color.Transparent

    CenterAlignedTopAppBar(
        title = { },
        navigationIcon = {
            IconButton(
                onClick = actions.onBackNavigation,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(buttonBackgroundColor, CircleShape)
            ) {
                Icon(
                    painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = iconColor
                )
            }
        },
        actions = {
            IconButton(
                onClick = actions.onShareClick,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(buttonBackgroundColor, CircleShape)
            ) {
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

@Composable
fun DetailsMainContent(
    state: MediaDetailsUiState,
    actions: DetailsActions,
    scrollState: ScrollState,
    paletteColors: PaletteColors?,
    onPaletteGenerated: (PaletteColors) -> Unit,
    onHeaderClick: () -> Unit,
    onReviewClick: (String, String) -> Unit,
    onAllTrailersClick: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        DetailsHeader(
            state = state,
            paletteColors = paletteColors,
            onPaletteGenerated = onPaletteGenerated,
            onHeaderClick = onHeaderClick
        )

        Column(modifier = Modifier.padding(16.dp)) {

            state.watchProviders?.let {
                WatchProvidersSection(it, paletteColors)
            }

            TrailersSection(
                youtubeTrailers = state.youtubeTrailers,
                paletteColors = paletteColors,
                onTrailerClick = actions.onTrailerClick,
                onPlusMoreClick = onAllTrailersClick
            )

            ActionsCard(
                state = state,
                actions = actions,
                paletteColors = paletteColors
            )

            if (state.ratings != null) {
                RatingsSection(state.ratings, paletteColors)
            }

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
            ReviewsSection(
                reviews = state.reviews,
                onViewAllClick = {
                    actions.onViewAllReviewsClick(state.mediaId, state.isTvShow, state.title)
                },
                onReviewClick = onReviewClick
            )
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
