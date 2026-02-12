package tech.salroid.filmy.ui.full

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.CastMovie
import tech.salroid.filmy.ui.cast_crew.CastCrewViewModel
import tech.salroid.filmy.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("No entries found.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(data.castMovies) { movie ->
                        MovieGridItem(movie, onClick = { onMovieClick(movie.id ?: 0, movie.title ?: "") })
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun MovieGridItem(movie: CastMovie, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = "http://image.tmdb.org/t/p/w342${movie.posterPath}",
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(0.66f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.default_banner_min)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = movie.title ?: "",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AllMoviesScreenPreview() {
    AppTheme {
        // Mocking
    }
}
