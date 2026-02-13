package tech.salroid.filmy.ui.movies.details

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
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.ui.cast_crew.CastCrewViewModel
import tech.salroid.filmy.ui.component.*
import tech.salroid.filmy.ui.details.MovieDetailsViewModel
import tech.salroid.filmy.ui.similar_recommendation.SimilarRecommendationViewModel
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.themeSystemBars

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    castViewModel: CastCrewViewModel = hiltViewModel(),
    similarViewModel: SimilarRecommendationViewModel = hiltViewModel(),
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit,
    onMovieClick: (Int) -> Unit,
    onBackNavigation: () -> Unit
) {
    val movieDetails by viewModel.uiStateMovieDetails.collectAsState()
    val reviews by viewModel.uiStateReviewResponse.collectAsState()
    val watchProviders by viewModel.uiStateWatchProvidersResponse.collectAsState()
    val castAndCrew by castViewModel.uiStateCastAndCrew.collectAsState()
    val similarMovies by similarViewModel.uiStateSimilar.collectAsState()
    val recommendations by similarViewModel.uiStateRecommendation.collectAsState()

    LaunchedEffect(movieId) {
        viewModel.getMovieDetails(movieId.toString(), 0)
        viewModel.getReviews(movieId.toString())
        viewModel.getWatchProviders(movieId.toString())
        castViewModel.getCastAndCrew(movieId.toString())
        similarViewModel.getSimilar(movieId.toString())
        similarViewModel.getRecommendation(movieId.toString())
    }

    MovieDetailsContent(
        movieDetails = movieDetails,
        reviews = reviews,
        watchProviders = watchProviders,
        castAndCrew = castAndCrew,
        similarMovies = similarMovies,
        recommendations = recommendations,
        onFavoriteToggle = { viewModel.toggleFavorite(it) },
        onWatchlistToggle = { viewModel.toggleWatchlist(it) },
        onViewAllCastClick = onViewAllCastClick,
        onMemberClick = onMemberClick,
        onMovieClick = onMovieClick,
        onBackNavigation = onBackNavigation,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailsContent(
    movieDetails: MovieDetails?,
    reviews: ReviewResponse?,
    watchProviders: WatchProviderResponse?,
    castAndCrew: CastAndCrewResponse?,
    similarMovies: SimilarMoviesResponse?,
    recommendations: SimilarMoviesResponse?,
    onFavoriteToggle: (MovieDetails) -> Unit,
    onWatchlistToggle: (MovieDetails) -> Unit,
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit,
    onMovieClick: (Int) -> Unit,
    onBackNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val imdbPrefix = stringResource(R.string.imdb_link_prefix)
    val scrollState = rememberScrollState()

    val scrollThreshold = with(LocalDensity.current) { 200.dp.toPx() }
    val visibilityModifier by remember {
        derivedStateOf {
            (1f - (scrollState.value / scrollThreshold)).coerceIn(0f, 1f)
        }
    }

    var paletteColors by remember(movieDetails?.id) { mutableStateOf<PaletteColors?>(null) }
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
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = fullReadContent.second,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showAllTrailers) {
        AllTrailersSheet(
            title = movieDetails?.title,
            trailers = movieDetails?.trailers?.youtube ?: emptyList(),
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
                        movieDetails?.let { onFavoriteToggle(it) }
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
                        movieDetails?.let { onWatchlistToggle(it) }
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
                        val movieImdb = imdbPrefix + (movieDetails?.imdbId ?: "")
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "*${movieDetails?.title}*\n${movieDetails?.tagline}\n$movieImdb\n"
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
        movieDetails?.let { movie ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                MovieDetailsHeader(
                    movie = movie,
                    paletteColors = paletteColors,
                    onPaletteGenerated = { paletteColors = it },
                    onHeaderClick = {
                        fullReadContent = Pair(movie.title ?: "", movie.overview ?: "")
                    }
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    WatchProvidersSection(watchProviders)

                    Text(
                        text = stringResource(R.string.ratings),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    RatingsSection(movie.voteAverage, movie.voteCount)

                    TrailersSection(
                        youtubeTrailers = movie.trailers?.youtube,
                        paletteColors = paletteColors,
                        onPlusMoreClick = { showAllTrailers = true }
                    )

                    CastSection(
                        castAndCrew?.cast,
                        movie.id,
                        movie.title ?: "",
                        false,
                        onViewAllCastClick,
                        onMemberClick
                    )
                    CrewSection(
                        castAndCrew?.crew,
                        movie.id,
                        movie.title ?: "",
                        false,
                        onViewAllCastClick,
                        onMemberClick
                    )
                    ReviewsSection(reviews) { title, content ->
                        fullReadContent = Pair(title, content)
                        showFullRead = true
                    }
                    MediaSuggestionsSection(
                        title = "Similar Movies",
                        response = similarMovies,
                        onMediaClick = onMovieClick
                    )
                    MediaSuggestionsSection(
                        title = "Recommendations",
                        response = recommendations,
                        onMediaClick = onMovieClick
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
fun MovieDetailsPreview() {
    AppTheme {
        MovieDetailsContent(
            movieDetails = MovieDetails(
                id = 123,
                title = "Inception",
                overview = "A thief who steals corporate secrets through the use of dream-sharing technology...",
                tagline = "Your mind is the scene of the crime",
                backdropPath = "/8Z99vYmda69uS0GE9u9EfwpS1QC.jpg",
                posterPath = "/9gk7Fn9sVAsS969O1o6oD9pS0pL.jpg",
                voteAverage = 8.8,
                voteCount = 30000,
                runtime = 148,
                releaseDate = "2010-07-16",
                originalLanguage = "en"
            ),
            reviews = null,
            watchProviders = null,
            castAndCrew = null,
            similarMovies = null,
            recommendations = null,
            onFavoriteToggle = {},
            onWatchlistToggle = {},
            onViewAllCastClick = { _, _, _ -> },
            onMemberClick = { _, _ -> },
            onMovieClick = {},
            onBackNavigation = {}
        )
    }
}
