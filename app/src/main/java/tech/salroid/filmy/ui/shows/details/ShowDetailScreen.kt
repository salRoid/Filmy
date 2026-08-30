package tech.salroid.filmy.ui.shows.details

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.salroid.filmy.ui.common.components.DetailsContent
import tech.salroid.filmy.ui.common.components.DetailsSkeletonLoader
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.details.MovieDetailsViewModel
import tech.salroid.filmy.ui.common.model.DetailsActions
import tech.salroid.filmy.ui.movies.details.components.RateMediaSheet
import tech.salroid.filmy.utility.openUrl
import tech.salroid.filmy.utility.openYoutubeTrailer
import tech.salroid.filmy.utility.shareMedia

@Composable
fun ShowDetailsScreen(
    showId: Int,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onViewAllReviewsClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit,
    onShowClick: (Int) -> Unit,
    onBackNavigation: () -> Unit,
    onSeasonClick: (Int, Int, String) -> Unit = { _, _, _ -> },
    onGalleryClick: (Int, Boolean) -> Unit = { _, _ -> },
    onKeywordClick: (Int, String) -> Unit = { _, _ -> }
) {
    val state by viewModel.mediaDetailsUiState.collectAsStateWithLifecycle()
    val showDetails by viewModel.uiStateTvDetails.collectAsStateWithLifecycle()
    val movieDetails by viewModel.uiStateMovieDetails.collectAsStateWithLifecycle()
    val isError by viewModel.uiStateError.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showRateSheet by remember { mutableStateOf(false) }

    LaunchedEffect(showId) {
        viewModel.fetchAllTvDetails(showId.toString(), 1)
    }

    Crossfade(
        targetState = state != null && showDetails != null,
        animationSpec = tween(durationMillis = 600),
        label = "details_fade"
    ) { isLoaded ->
        if (isLoaded) {
            val currentState = state
            val currentShowDetails = showDetails
            val currentMovieDetails = movieDetails

            if (currentState != null && currentShowDetails != null) {
                val actions = DetailsActions(
                    onWatchedToggle = { viewModel.toggleWatchedTv(currentShowDetails, currentMovieDetails) },
                    onWatchlistToggle = { viewModel.toggleWatchlistTv(currentShowDetails, currentMovieDetails) },
                    onViewAllCastClick = onViewAllCastClick,
                    onViewAllReviewsClick = onViewAllReviewsClick,
                    onMemberClick = onMemberClick,
                    onMediaClick = onShowClick,
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
                    onSeasonClick = onSeasonClick,
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

                if (showRateSheet) {
                    RateMediaSheet(
                        currentRating = currentState.userRating,
                        onSubmit = { rating ->
                            viewModel.rateTvShow(currentShowDetails, currentMovieDetails, rating)
                            showRateSheet = false
                        },
                        onRemove = {
                            viewModel.rateTvShow(currentShowDetails, currentMovieDetails, null)
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
                onRetryClick = { viewModel.fetchAllTvDetails(showId.toString(), 1) },
                onBackClick = onBackNavigation
            )
        } else {
            DetailsSkeletonLoader(modifier = modifier)
        }
    }
}
