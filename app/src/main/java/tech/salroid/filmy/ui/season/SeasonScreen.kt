package tech.salroid.filmy.ui.season

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.tv.Episode
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.common.model.WatchProvidersUiModel
import tech.salroid.filmy.ui.movies.details.components.WatchProvidersSection
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
    val episodeRatings by viewModel.episodeRatings.collectAsStateWithLifecycle()
    val watchProviders by viewModel.watchProviders.collectAsStateWithLifecycle()

    LaunchedEffect(tvId, seasonNumber) {
        viewModel.loadSeason(tvId.toString(), seasonNumber)
    }

    SeasonContent(
        title = season?.name ?: showTitle,
        episodes = season?.episodes ?: emptyList(),
        episodeRatings = episodeRatings,
        watchProviders = watchProviders,
        isLoading = isLoading,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonContent(
    title: String,
    episodes: List<Episode>,
    episodeRatings: Map<Int, String> = emptyMap(),
    watchProviders: WatchProvidersUiModel? = null,
    isLoading: Boolean,
    onBackClick: () -> Unit
) {
    var showWatchProviders by remember { mutableStateOf(false) }

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
                        EpisodeItem(
                            episode,
                            imdbRating = episode.episodeNumber?.let { episodeRatings[it] },
                            onClick = { showWatchProviders = true }
                        )
                    }
                }
            }
        }
    }

    if (showWatchProviders) {
        EpisodeWatchProvidersSheet(
            watchProviders = watchProviders,
            onDismiss = { showWatchProviders = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EpisodeWatchProvidersSheet(
    watchProviders: WatchProvidersUiModel?,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            if (watchProviders != null && watchProviders.providers.isNotEmpty()) {
                WatchProvidersSection(watchProviders = watchProviders)
            } else {
                Text(
                    text = stringResource(R.string.no_watch_providers_available),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun EpisodeItem(episode: Episode, imdbRating: String? = null, onClick: () -> Unit = {}) {
    val runtimeText = episode.runtime?.takeIf { it > 0 }?.let { "${it}m" }
    val airDateText = episode.airDate?.toReadableDate()
    val metaText = listOfNotNull(runtimeText, airDateText).joinToString(" • ")

    Card(
        onClick = onClick,
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
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.poster_error_placeholder)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${episode.episodeNumber ?: 0}. ${episode.name ?: ""}",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                if (imdbRating != null) {
                    Row(
                        modifier = Modifier.padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.imdb),
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Text(
                            text = imdbRating,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .alpha(0.8f)
                        )
                    }
                }
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
