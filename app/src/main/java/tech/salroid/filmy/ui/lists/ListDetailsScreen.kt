package tech.salroid.filmy.ui.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.common.components.PreviewList
import tech.salroid.filmy.utility.toReadableDate

@Composable
fun ListDetailsScreen(
    listId: Int,
    title: String,
    viewModel: ListDetailsViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var movieToRemove by remember { mutableStateOf<Movie?>(null) }

    LaunchedEffect(listId) {
        viewModel.loadListDetails(listId)
    }

    ListDetailsContent(
        title = title,
        items = items,
        isLoading = isLoading,
        onMovieClick = { onMovieClick(it.id) },
        onMovieLongClick = { movieToRemove = it },
        onBackClick = onBackClick
    )

    movieToRemove?.let { movie ->
        AlertDialog(
            onDismissRequest = { movieToRemove = null },
            title = { Text(stringResource(R.string.remove)) },
            text = { Text(movie.title ?: "") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.removeItem(listId, movie)
                    movieToRemove = null
                }) {
                    Text(stringResource(R.string.remove))
                }
            },
            dismissButton = {
                TextButton(onClick = { movieToRemove = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailsContent(
    title: String,
    items: List<Movie>,
    isLoading: Boolean,
    onMovieClick: (Movie) -> Unit,
    onMovieLongClick: (Movie) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingWidget(modifier = Modifier.padding(paddingValues).fillMaxSize())
            }
            items.isEmpty() -> {
                Box(
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.list_empty))
                }
            }
            else -> {
                PreviewList(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(top = 16.dp),
                    items = items.size
                ) { index ->
                    val movie = items[index]
                    ListMovieItem(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        onLongClick = { onMovieLongClick(movie) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListMovieItem(
    movie: Movie,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(corner = CornerSize(8.dp))),
                contentScale = ContentScale.Crop,
                model = stringResource(R.string.movie_poster_url, movie.posterPath ?: ""),
                contentDescription = movie.title
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                text = movie.title ?: ""
            )
            Text(
                modifier = Modifier.padding(top = 4.dp).alpha(0.8f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                text = movie.releaseDate?.toReadableDate() ?: ""
            )
        }
    }
}
