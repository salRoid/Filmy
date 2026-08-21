package tech.salroid.filmy.ui.movies.details

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.salroid.filmy.ui.common.components.DetailsContent
import tech.salroid.filmy.ui.common.components.DetailsSkeletonLoader
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.common.components.LoginRequiredDialog
import tech.salroid.filmy.ui.details.MovieDetailsViewModel
import tech.salroid.filmy.ui.common.model.DetailsActions
import tech.salroid.filmy.ui.home.LoginViewModel
import tech.salroid.filmy.ui.home.rememberLoginLauncher
import tech.salroid.filmy.ui.movies.details.components.AddToListSheet
import tech.salroid.filmy.ui.movies.details.components.RateMediaSheet
import tech.salroid.filmy.utility.openUrl
import tech.salroid.filmy.utility.openYoutubeTrailer
import tech.salroid.filmy.utility.shareMedia

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onViewAllReviewsClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit,
    onMovieClick: (Int) -> Unit,
    onBackNavigation: () -> Unit,
    onCollectionClick: (Int, String) -> Unit = { _, _ -> },
    onGalleryClick: (Int, Boolean) -> Unit = { _, _ -> },
    onKeywordClick: (Int, String) -> Unit = { _, _ -> }
) {
    val state by viewModel.mediaDetailsUiState.collectAsStateWithLifecycle()
    val movieDetails by viewModel.uiStateMovieDetails.collectAsStateWithLifecycle()
    val isError by viewModel.uiStateError.collectAsStateWithLifecycle()
    val userLists by viewModel.userLists.collectAsStateWithLifecycle()
    val listMembership by viewModel.listMembership.collectAsStateWithLifecycle()
    val loginProfile by loginViewModel.uiStateProfile.collectAsStateWithLifecycle()
    val startLogin = rememberLoginLauncher(loginViewModel)
    val context = LocalContext.current

    var showAddToListSheet by remember { mutableStateOf(false) }
    var showRateSheet by remember { mutableStateOf(false) }
    var showLoginPrompt by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        viewModel.fetchAllMovieDetails(movieId.toString(), 0)
    }

    // Logging in here opens the list sheet right away with fresh data -
    // no need to leave the details screen and come back.
    LaunchedEffect(loginProfile) {
        if (loginProfile != null && showLoginPrompt) {
            showLoginPrompt = false
            viewModel.loadUserLists(movieId)
            showAddToListSheet = true
        }
    }

    Crossfade(
        targetState = state != null && movieDetails != null,
        animationSpec = tween(durationMillis = 600),
        label = "details_fade"
    ) { isLoaded ->
        if (isLoaded) {
            val currentState = state
            val currentMovieDetails = movieDetails
            if (currentState != null && currentMovieDetails != null) {
                val actions = DetailsActions(
                    onWatchedToggle = { viewModel.toggleWatched(currentMovieDetails) },
                    onWatchlistToggle = { viewModel.toggleWatchlist(currentMovieDetails) },
                    onViewAllCastClick = onViewAllCastClick,
                    onViewAllReviewsClick = onViewAllReviewsClick,
                    onMemberClick = onMemberClick,
                    onMediaClick = onMovieClick,
                    onTrailerClick = { context.openYoutubeTrailer(it) },
                    onShareClick = {
                        context.shareMedia(
                            title = currentState.title,
                            tagline = currentState.tagline,
                            imdbId = currentState.imdbId,
                            isTvShow = currentState.isTvShow
                        )
                    },
                    onBackNavigation = onBackNavigation,
                    onAddToListClick = {
                        if (viewModel.isLoggedIn()) {
                            viewModel.loadUserLists(movieId)
                            showAddToListSheet = true
                        } else {
                            showLoginPrompt = true
                        }
                    },
                    onCollectionClick = onCollectionClick,
                    onRateClick = { showRateSheet = true },
                    onGalleryClick = onGalleryClick,
                    onKeywordClick = onKeywordClick,
                    onLinkClick = { context.openUrl(it) }
                )

                DetailsContent(
                    state = currentState,
                    actions = actions,
                    modifier = modifier
                )

                if (showAddToListSheet) {
                    AddToListSheet(
                        lists = userLists,
                        membership = listMembership,
                        onToggle = { listId, currentlyIn ->
                            viewModel.toggleListMembership(listId, movieId, currentlyIn)
                        },
                        onCreateList = { name -> viewModel.createList(name) },
                        onDismiss = { showAddToListSheet = false }
                    )
                }

                if (showRateSheet) {
                    RateMediaSheet(
                        currentRating = currentState.userRating,
                        onSubmit = { rating ->
                            viewModel.rateMovie(currentMovieDetails, rating)
                            showRateSheet = false
                        },
                        onRemove = {
                            viewModel.rateMovie(currentMovieDetails, null)
                            showRateSheet = false
                        },
                        onDismiss = { showRateSheet = false }
                    )
                }
            }
        } else if (isError) {
            ErrorWidget(
                modifier = modifier,
                message = "Couldn't load details. Check your connection.",
                onRetryClick = { viewModel.fetchAllMovieDetails(movieId.toString(), 0) }
            )
        } else {
            DetailsSkeletonLoader(
                modifier = modifier,
                onBackClick = onBackNavigation
            )
        }
    }

    if (showLoginPrompt) {
        LoginRequiredDialog(
            message = "Log in to your TMDB account to add this to a list.",
            onLogin = startLogin,
            onDismiss = { showLoginPrompt = false }
        )
    }
}
