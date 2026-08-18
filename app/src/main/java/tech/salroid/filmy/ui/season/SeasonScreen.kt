package tech.salroid.filmy.ui.season

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.tv.Episode
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.utility.toReadableDate

@Composable
fun SeasonScreen(
    tvId: Int,
    seasonNumber: Int,
    showTitle: String,
    viewModel: SeasonViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val season by viewModel.season.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(tvId, seasonNumber) {
        viewModel.loadSeason(tvId.toString(), seasonNumber)
    }

    SeasonContent(
        title = season?.name ?: showTitle,
        episodes = season?.episodes ?: emptyList(),
        isLoading = isLoading,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonContent(
    title: String,
    episodes: List<Episode>,
    isLoading: Boolean,
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
            episodes.isEmpty() -> {
                Box(
                    modifier = Modifier.padding(paddingValues).fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.list_empty))
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(episodes) { episode ->
                        EpisodeItem(episode)
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeItem(episode: Episode) {
    val runtimeText = episode.runtime?.takeIf { it > 0 }?.let { "${it}m" }
    val airDateText = episode.airDate?.toReadableDate()
    val metaText = listOfNotNull(runtimeText, airDateText).joinToString(" • ")

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column {
            AsyncImage(
                model = stringResource(R.string.movie_poster_url, episode.stillPath ?: ""),
                contentDescription = episode.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "${episode.episodeNumber ?: 0}. ${episode.name ?: ""}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.weight(1f)
                )
            }

            if (metaText.isNotEmpty()) {
                Text(
                    text = metaText,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .alpha(0.6f)
                )
            }

            if (!episode.overview.isNullOrEmpty()) {
                Text(
                    text = episode.overview ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .alpha(0.8f)
                )
            }
        }
    }
}
