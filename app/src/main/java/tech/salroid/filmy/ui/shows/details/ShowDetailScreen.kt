package tech.salroid.filmy.ui.shows.details

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.salroid.filmy.ui.common.components.DetailsContent
import tech.salroid.filmy.ui.common.components.DetailsSkeletonLoader
import tech.salroid.filmy.ui.details.MovieDetailsViewModel
import tech.salroid.filmy.ui.common.model.DetailsActions

@Composable
fun ShowDetailsScreen(
    showId: Int,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit,
    onShowClick: (Int) -> Unit,
    onBackNavigation: () -> Unit
) {
    val state by viewModel.mediaDetailsUiState.collectAsStateWithLifecycle()
    val showDetails by viewModel.uiStateTvDetails.collectAsStateWithLifecycle()
    val movieDetails by viewModel.uiStateMovieDetails.collectAsStateWithLifecycle()

    LaunchedEffect(showId) {
        viewModel.fetchAllTvDetails(showId.toString(), 1)
    }

    Crossfade(
        targetState = state != null && showDetails != null,
        animationSpec = tween(durationMillis = 600),
        label = "details_fade"
    ) { isLoaded ->
        if (isLoaded) {
            val actions = DetailsActions(
                onFavoriteToggle = { showDetails?.let { viewModel.toggleFavoriteTv(it, movieDetails) } },
                onWatchlistToggle = { showDetails?.let { viewModel.toggleWatchlistTv(it, movieDetails) } },
                onViewAllCastClick = onViewAllCastClick,
                onMemberClick = onMemberClick,
                onMediaClick = onShowClick,
                onBackNavigation = onBackNavigation
            )

            DetailsContent(
                state = state!!,
                actions = actions,
                modifier = modifier
            )
        } else {
            DetailsSkeletonLoader(
                modifier = modifier,
                onBackClick = onBackNavigation
            )
        }
    }
}
