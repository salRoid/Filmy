package tech.salroid.filmy.ui.movies

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MoviesRoute(
    modifier: Modifier = Modifier,
    viewModel: MoviesViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    MoviesScreen(
        modifier = modifier,
        state = state,
        onMovieClick = onMovieClick,
        onRetry = viewModel::retry
    )
}