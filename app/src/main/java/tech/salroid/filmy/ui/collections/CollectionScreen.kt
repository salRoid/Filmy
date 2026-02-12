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
import kotlinx.coroutines.launch
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.ui.collections.components.CollectionItem
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun CollectionScreen(
    viewModel: CollectionsViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit,
) {
    val favorites by viewModel.favorites.collectAsState()
    val watchlist by viewModel.watchlist.collectAsState()

    CollectionScreenContent(
        favorites = favorites,
        watchlist = watchlist,
        onMovieClick = onMovieClick
    )
}

@Composable
fun CollectionScreenContent(
    favorites: List<MovieDetails>,
    watchlist: List<MovieDetails>,
    onMovieClick: (Int) -> Unit
) {
    val tabs = listOf(stringResource(R.string.favourite), stringResource(R.string.watchlist))
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
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
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val currentList = if (page == 0) favorites else watchlist
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
                    items(currentList, key = { it.id }) { movie ->
                        CollectionItem(
                            movie,
                            onClick = { onMovieClick(movie.id) }
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
    AppTheme {
        CollectionScreenContent(
            favorites = listOf(
                MovieDetails(
                    id = 1,
                    title = "SpiderMan: India?",
                    posterPath = "",
                    releaseDate = "2023-01-22",
                    overview = "A small, wealthy family in New York City gets progressively torn apart by secrets, lies, and the theft that orchestrates all of it."
                )
            ),
            watchlist = emptyList(),
            onMovieClick = {}
        )
    }
}
