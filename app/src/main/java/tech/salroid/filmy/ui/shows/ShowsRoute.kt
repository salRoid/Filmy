package tech.salroid.filmy.ui.shows

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ShowsRoute(
    modifier: Modifier = Modifier,
    viewModel: ShowsViewModel = hiltViewModel(),
    onShowClick: (Int) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ShowsScreen(
        modifier = modifier,
        state = state,
        onShowClick = onShowClick,
        onRetry = viewModel::retry
    )
}