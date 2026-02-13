package tech.salroid.filmy.ui.shows.details

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.ui.cast_crew.CastCrewViewModel
import tech.salroid.filmy.ui.component.*
import tech.salroid.filmy.ui.details.MovieDetailsViewModel
import tech.salroid.filmy.ui.similar_recommendation.SimilarRecommendationViewModel
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.themeSystemBars
import tech.salroid.filmy.utility.toReadableDate

@Composable
fun ShowDetailsScreen(
    showId: Int,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    castViewModel: CastCrewViewModel = hiltViewModel(),
    similarViewModel: SimilarRecommendationViewModel = hiltViewModel(),
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit,
    onShowClick: (Int) -> Unit,
    onBackNavigation: () -> Unit
) {
    val showDetails by viewModel.uiStateTvDetails.collectAsState()
    val movieDetails by viewModel.uiStateMovieDetails.collectAsState()
    val reviews by viewModel.uiStateReviewResponse.collectAsState()
    val watchProviders by viewModel.uiStateWatchProvidersResponse.collectAsState()
    val castAndCrew by castViewModel.uiStateCastAndCrew.collectAsState()
    val similarShows by similarViewModel.uiStateSimilar.collectAsState()
    val recommendations by similarViewModel.uiStateRecommendation.collectAsState()

    LaunchedEffect(showId) {
        viewModel.getTvDetails(showId.toString(), 1) // 1 for TV shows
        viewModel.getTvReviews(showId.toString())
        viewModel.getWatchProvidersTv(showId.toString())
        castViewModel.getCastAndCrewTv(showId.toString())
        similarViewModel.getSimilarTv(showId.toString())
        similarViewModel.getRecommendationTv(showId.toString())
    }

    ShowDetailsContent(
        showDetails = showDetails,
        movieDetails = movieDetails,
        reviews = reviews,
        watchProviders = watchProviders,
        castAndCrew = castAndCrew,
        similarShows = similarShows,
        recommendations = recommendations,
        onFavoriteToggle = { viewModel.toggleFavoriteTv(it, movieDetails) },
        onWatchlistToggle = { viewModel.toggleWatchlistTv(it, movieDetails) },
        onViewAllCastClick = onViewAllCastClick,
        onMemberClick = onMemberClick,
        onShowClick = onShowClick,
        onBackNavigation = onBackNavigation,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowDetailsContent(
    showDetails: TvDetails?,
    movieDetails: MovieDetails?,
    reviews: ReviewResponse?,
    watchProviders: WatchProviderResponse?,
    castAndCrew: CastAndCrewResponse?,
    similarShows: SimilarMoviesResponse?,
    recommendations: SimilarMoviesResponse?,
    onFavoriteToggle: (TvDetails) -> Unit,
    onWatchlistToggle: (TvDetails) -> Unit,
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit,
    onShowClick: (Int) -> Unit,
    onBackNavigation: () -> Unit,
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

    var paletteColors by remember(showDetails?.id) { mutableStateOf<PaletteColors?>(null) }
    var showFullRead by remember { mutableStateOf(false) }
    var showAllTrailers by remember { mutableStateOf(false) }
    var fullReadContent by remember { mutableStateOf(Pair("", "")) }

    val iconColor =
        if (visibilityModifier > 0.5f) Color.White else MaterialTheme.colorScheme.onSurface
    val containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f - visibilityModifier)

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
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
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
            title = showDetails?.name,
            trailers = showDetails?.trailers?.youtube ?: emptyList(),
            onDismiss = { showAllTrailers = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackNavigation) {
                        Icon(
                            painterResource(R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = iconColor
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        showDetails?.let { onFavoriteToggle(it) }
                    }) {
                        Icon(
                            painter = if (movieDetails?.favorite == true) painterResource(R.drawable.ic_round_favorite_24) else painterResource(
                                R.drawable.ic_round_favorite_border_24
                            ),
                            contentDescription = "Favorite",
                            tint = if (movieDetails?.favorite == true) Color.Red else iconColor
                        )
                    }
                    IconButton(onClick = {
                        showDetails?.let { onWatchlistToggle(it) }
                    }) {
                        Icon(
                            painter = if (movieDetails?.watchlist == true) painterResource(R.drawable.ic_round_bookmark_added_24) else painterResource(
                                R.drawable.ic_round_bookmark_add_24
                            ),
                            contentDescription = "Watchlist",
                            tint = iconColor
                        )
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "*${showDetails?.name}*\n${showDetails?.tagline}\n"
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
        showDetails?.let { show ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                TvDetailsHeader(
                    show = show,
                    paletteColors = paletteColors,
                    onPaletteGenerated = { paletteColors = it },
                    onHeaderClick = {
                        fullReadContent = Pair(show.name ?: "", show.overview ?: "")
                        showFullRead = true
                    }
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    WatchProvidersSection(watchProviders)

                    Text(
                        text = stringResource(R.string.ratings),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    RatingsSection(show.voteAverage, show.voteCount?.toLong())

                    TrailersSection(
                        youtubeTrailers = show.trailers?.youtube,
                        paletteColors = paletteColors,
                        onPlusMoreClick = { showAllTrailers = true }
                    )

                    CastSection(
                        castAndCrew?.cast,
                        show.id ?: 0,
                        show.name ?: "",
                        true,
                        onViewAllCastClick,
                        onMemberClick
                    )
                    CrewSection(
                        castAndCrew?.crew,
                        show.id ?: 0,
                        show.name ?: "",
                        true,
                        onViewAllCastClick,
                        onMemberClick
                    )
                    ReviewsSection(reviews) { title, content ->
                        fullReadContent = Pair(title, content)
                        showFullRead = true
                    }
                    MediaSuggestionsSection(
                        title = "Similar Shows",
                        response = similarShows,
                        onMediaClick = onShowClick
                    )
                    MediaSuggestionsSection(
                        title = "Recommendations",
                        response = recommendations,
                        onMediaClick = onShowClick
                    )
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShowDetailsPreview() {
    AppTheme {
        ShowDetailsContent(
            showDetails = TvDetails(
                id = 123,
                name = "Breaking Bad",
                overview = "A high school chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine in order to secure his family's future.",
                tagline = "All Hail the King",
                backdropPath = "/8Z99vYmda69uS0GE9u9EfwpS1QC.jpg",
                posterPath = "/9gk7Fn9sVAsS969O1o6oD9pS0pL.jpg",
                voteAverage = 9.5,
                voteCount = 10000,
                firstAirDate = "2008-01-20",
                originalLanguage = "en"
            ),
            movieDetails = null,
            reviews = null,
            watchProviders = null,
            castAndCrew = null,
            similarShows = null,
            recommendations = null,
            onFavoriteToggle = {},
            onWatchlistToggle = {},
            onViewAllCastClick = { _, _, _ -> },
            onMemberClick = { _, _ -> },
            onShowClick = {},
            onBackNavigation = {}
        )
    }
}
