package tech.salroid.filmy.ui.collections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.ui.collections.components.CollectionItem
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun CollectionScreen(
    modifier: Modifier = Modifier,
    viewModel: CollectionsViewModel = hiltViewModel(),
    onMovieClick: (Int, Int) -> Unit,
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var selectedMovie by remember { mutableStateOf<MovieDetails?>(null) }
    val pagerState = rememberPagerState(pageCount = { 2 })

    if (showDialog && selectedMovie != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.remove_from_collection)) },
            text = {
                Text(
                    stringResource(
                        if (pagerState.currentPage == 0) R.string.remove_fav_message
                        else R.string.remove_watchlist_message
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val movie = selectedMovie
                    if (movie != null) {
                        if (pagerState.currentPage == 0) {
                            viewModel.removeFavorite(movie)
                        } else {
                            viewModel.removeWatchlist(movie)
                        }
                    }
                    showDialog = false
                }) {
                    Text(stringResource(R.string.remove))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    CollectionScreenContent(
        modifier = modifier,
        favorites = favorites,
        watchlist = watchlist,
        pagerState = pagerState,
        onMovieClick = onMovieClick,
        onMovieLongClick = { movie ->
            selectedMovie = movie
            showDialog = true
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionScreenContent(
    favorites: List<MovieDetails>,
    watchlist: List<MovieDetails>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    onMovieClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    onMovieLongClick: (MovieDetails) -> Unit = {}
) {
    val tabs = listOf(stringResource(R.string.favourite), stringResource(R.string.watchlist))
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {
                        Text(
                            title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (pagerState.currentPage == index) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            }
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            val listToDisplay = if (page == 0) favorites else watchlist
            val currentList = remember(listToDisplay) { listToDisplay.reversed() }
            
            val emptyMessage = if (page == 0)
                stringResource(R.string.your_fav_movies_appear_here)
            else
                stringResource(R.string.your_watchlist_movies_appear_here)

            if (currentList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                ) {
                    items(currentList, key = { "${it.id}_${it.type}" }) { movie ->
                        CollectionItem(
                            movie,
                            onClick = { onMovieClick(movie.id, movie.type) },
                            onLongClick = { onMovieLongClick(movie) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CollectionScreenPreview() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    AppTheme {
        CollectionScreenContent(
            favorites = listOf(
                MovieDetails(
                    id = 1,
                    title = "SpiderMan: India?",
                    posterPath = "/path.jpg",
                    releaseDate = "2023-01-22",
                    overview = "Sample overview"
                )
            ),
            watchlist = emptyList(),
            pagerState = pagerState,
            onMovieClick = { _, _ -> }
        )
    }
}
