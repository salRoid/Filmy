package tech.salroid.filmy.ui.full

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastMovie
import tech.salroid.filmy.data.local.model.CastCrewMoviesResponse
import tech.salroid.filmy.ui.cast_crew.CastCrewViewModel
import tech.salroid.filmy.ui.common.components.PreviewItem
import tech.salroid.filmy.ui.common.components.PreviewList
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import tech.salroid.filmy.ui.LocalWindowSizeClass
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.toReadableDate

@Composable
fun AllMoviesScreen(
    memberId: Int,
    isTv: Boolean,
    title: String,
    viewModel: CastCrewViewModel = hiltViewModel(),
    onMovieClick: (Int, String) -> Unit,
    onBackClick: () -> Unit
) {
    val moviesResponse by viewModel.uiStateCastCrewMovies.collectAsState()

    LaunchedEffect(memberId, isTv) {
        if (isTv) {
            viewModel.getCastCrewTvShows(memberId.toString())
        } else {
            viewModel.getCastCrewMovies(memberId.toString())
        }
    }

    AllMoviesContent(
        title = title,
        moviesResponse = moviesResponse,
        onMovieClick = onMovieClick,
        onBackClick = onBackClick
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun AllMoviesContent(
    title: String,
    moviesResponse: CastCrewMoviesResponse?,
    onMovieClick: (Int, String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        moviesResponse?.let { data ->
            if (data.castMovies.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No entries found.")
                }
            } else {
                PreviewList(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(top = 16.dp),
                    items = data.castMovies.size
                ) { index ->
                    val movie = data.castMovies[index]
                    PreviewItem(
                        title = movie.title ?: movie.name ?: "",
                        posterUrl = "https://image.tmdb.org/t/p/w342${movie.posterPath}",
                        readableDate = movie.releaseDate?.toReadableDate() ?: "",
                        onItemClick = { onMovieClick(movie.id ?: 0, movie.title ?: "") }
                    )
                }
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AllMoviesScreenPreview() {
    val sampleMovies = CastCrewMoviesResponse(
        castMovies = arrayListOf(
            CastMovie(id = 1, title = "Fight Club", posterPath = null),
            CastMovie(id = 2, title = "Seven", posterPath = null),
            CastMovie(id = 3, title = "Inglourious Basterds", posterPath = null),
            CastMovie(id = 4, title = "Once Upon a Time in Hollywood", posterPath = null),
            CastMovie(id = 5, title = "Moneyball", posterPath = null),
            CastMovie(id = 6, title = "Ad Astra", posterPath = null)
        )
    )
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        AppTheme {
            AllMoviesContent(
                title = "Brad Pitt",
                moviesResponse = sampleMovies,
                onMovieClick = { _, _ -> },
                onBackClick = {}
            )
        }
    }
}
