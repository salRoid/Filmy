package tech.salroid.filmy.ui.franchise

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.collection.CollectionMovie
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.common.components.PreviewList
import tech.salroid.filmy.utility.toReadableDate

@Composable
fun FranchiseScreen(
    collectionId: Int,
    title: String,
    viewModel: FranchiseViewModel = hiltViewModel(),
    onMovieClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val collection by viewModel.collection.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(collectionId) {
        viewModel.loadCollection(collectionId)
    }

    FranchiseContent(
        title = collection?.name ?: title,
        items = collection?.parts ?: emptyList(),
        isLoading = isLoading,
        onMovieClick = onMovieClick,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FranchiseContent(
    title: String,
    items: List<CollectionMovie>,
    isLoading: Boolean,
    onMovieClick: (Int) -> Unit,
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
                    FranchiseMovieItem(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FranchiseMovieItem(
    movie: CollectionMovie,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.clickable(onClick = onClick),
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
