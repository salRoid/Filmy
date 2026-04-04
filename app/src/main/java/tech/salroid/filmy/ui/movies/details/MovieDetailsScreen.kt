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
import tech.salroid.filmy.ui.details.MovieDetailsViewModel
import tech.salroid.filmy.ui.common.model.DetailsActions
import tech.salroid.filmy.utility.openYoutubeTrailer
import tech.salroid.filmy.utility.shareMedia

@Composable
fun MovieDetailsScreen(
    movieId: Int,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onViewAllReviewsClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit,
    onMovieClick: (Int) -> Unit,
    onBackNavigation: () -> Unit
) {
    val state by viewModel.mediaDetailsUiState.collectAsStateWithLifecycle()
    val movieDetails by viewModel.uiStateMovieDetails.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(movieId) {
        viewModel.fetchAllMovieDetails(movieId.toString(), 0)
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
                    onBackNavigation = onBackNavigation
                )

                DetailsContent(
                    state = currentState,
                    actions = actions,
                    modifier = modifier
                )
            }
        } else {
            DetailsSkeletonLoader(
                modifier = modifier,
                onBackClick = onBackNavigation
            )
        }
    }
}
