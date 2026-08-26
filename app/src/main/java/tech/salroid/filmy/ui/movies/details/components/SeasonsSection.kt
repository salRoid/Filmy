package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.SeasonUiModel
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun SeasonsSection(
    seasons: List<SeasonUiModel>?,
    onSeasonClick: (Int) -> Unit
) {
    val visibleSeasons = seasons?.filter { it.episodeCount > 0 }
    if (!visibleSeasons.isNullOrEmpty()) {
        DetailsSection(title = "Seasons") {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(visibleSeasons) { season ->
                    SeasonItem(season) { onSeasonClick(season.seasonNumber) }
                }
            }
        }
    }
}

@Composable
private fun SeasonItem(
    season: SeasonUiModel,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = stringResource(R.string.movie_poster_url, season.posterPath ?: ""),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp, 145.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.poster_error_placeholder)
        )
        Text(
            text = season.name,
            style = MaterialTheme.typography.labelSmall,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "${season.episodeCount} Episodes",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.alpha(0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SeasonsSectionPreview() {
    AppTheme {
        SeasonsSection(
            seasons = listOf(
                SeasonUiModel(1, 1, "Season 1", 8, "/edv5bs1pS9v796LpT2M0sYhC76B.jpg", "2020-01-01"),
                SeasonUiModel(2, 2, "Season 2", 10, "/edv5bs1pS9v796LpT2M0sYhC76B.jpg", "2021-01-01")
            ),
            onSeasonClick = {}
        )
    }
}
