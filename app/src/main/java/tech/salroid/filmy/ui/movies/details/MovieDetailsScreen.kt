package tech.salroid.filmy.ui.movies.details

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
fun MovieDetailsScreen(
    movieId: Int,
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
    onViewAllCastClick: (Int, Boolean, String) -> Unit,
    onMemberClick: (Int, Boolean) -> Unit,
    onMovieClick: (Int) -> Unit,
    onBackNavigation: () -> Unit
) {
    val state by viewModel.mediaDetailsUiState.collectAsStateWithLifecycle()
    val movieDetails by viewModel.uiStateMovieDetails.collectAsStateWithLifecycle()

    LaunchedEffect(movieId) {
        viewModel.fetchAllMovieDetails(movieId.toString(), 0)
    }

    Crossfade(
        targetState = state != null && movieDetails != null,
        animationSpec = tween(durationMillis = 600),
        label = "details_fade"
    ) { isLoaded ->
        if (isLoaded) {
            val actions = DetailsActions(
                onFavoriteToggle = { movieDetails?.let { viewModel.toggleFavorite(it) } },
                onWatchlistToggle = { movieDetails?.let { viewModel.toggleWatchlist(it) } },
                onViewAllCastClick = onViewAllCastClick,
                onMemberClick = onMemberClick,
                onMediaClick = onMovieClick,
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
