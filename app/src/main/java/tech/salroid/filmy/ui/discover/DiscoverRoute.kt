package tech.salroid.filmy.ui.discover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun DiscoverRoute(
    isTv: Boolean,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onShowClick: (Int) -> Unit,
    keywordId: Int? = null,
    keywordName: String? = null,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    LaunchedEffect(isTv) {
        viewModel.initialize(isTv, keywordId, keywordName)
    }

    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val genres by viewModel.genres.collectAsStateWithLifecycle()
    val movieItems = if (!isTv) viewModel.moviesResults.collectAsLazyPagingItems() else null
    val showItems = if (isTv) viewModel.showsResults.collectAsLazyPagingItems() else null

    DiscoverScreen(
        isTv = isTv,
        movieItems = movieItems,
        showItems = showItems,
        filters = filters,
        genres = genres,
        onApplyFilters = viewModel::applyFilters,
        onBackClick = onBackClick,
        onMovieClick = onMovieClick,
        onShowClick = onShowClick
    )
}
