package tech.salroid.filmy.ui.movies.details

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.components.DetailsContent
import tech.salroid.filmy.ui.common.components.DetailsSkeletonLoader
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.details.MovieDetailsViewModel
import tech.salroid.filmy.ui.common.model.DetailsActions
import tech.salroid.filmy.ui.home.LoginViewModel
import tech.salroid.filmy.ui.movies.details.components.AddToListFlow
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
    val context = LocalContext.current

    var showAddToList by remember { mutableStateOf(false) }
    var showRateSheet by remember { mutableStateOf(false) }

    LaunchedEffect(movieId) {
        viewModel.fetchAllMovieDetails(movieId.toString(), 0)
    }

    // Checks the user's lists up front so "Add to List" can show that this
    // title is already in one - and again after a login from this screen.
    val canManageLists by loginViewModel.canManageLists.collectAsStateWithLifecycle()
    LaunchedEffect(movieId, canManageLists) {
        if (canManageLists) {
            viewModel.loadUserLists(movieId, isTv = false)
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
                    onAddToListClick = { showAddToList = true },
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

                if (showAddToList) {
                    AddToListFlow(
                        mediaId = movieId,
                        isTv = false,
                        viewModel = viewModel,
                        loginViewModel = loginViewModel,
                        onDismiss = { showAddToList = false }
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
                message = stringResource(R.string.couldnt_load_details),
                onRetryClick = { viewModel.fetchAllMovieDetails(movieId.toString(), 0) },
                onBackClick = onBackNavigation
            )
        } else {
            DetailsSkeletonLoader(modifier = modifier)
        }
    }
}
