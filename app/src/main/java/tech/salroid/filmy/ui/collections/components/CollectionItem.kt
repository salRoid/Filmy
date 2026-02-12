package tech.salroid.filmy.ui.collections.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import tech.salroid.filmy.utility.toReadableDate
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import androidx.compose.ui.tooling.preview.Preview
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun CollectionItem(
    movie: MovieDetails,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .dropShadow(
                shape = RoundedCornerShape(12.dp),
                shadow = Shadow(
                    radius = 16.dp,
                    spread = 0.dp,
                    offset = DpOffset(0.dp, 4.dp),
                    alpha = 0.06f,
                    color = MaterialTheme.colorScheme.scrim
                )
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = stringResource(R.string.movie_poster_url, movie.posterPath ?: ""),
                contentDescription = movie.title,
                modifier = Modifier
                    .width(105.dp)
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.movie_skeleton)
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = movie.title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = movie.releaseDate?.toReadableDate() ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = movie.overview ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .alpha(0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CollectionItemPreview() {
    AppTheme {
        CollectionItem(
            movie = MovieDetails(
                id = 1,
                title = "SpiderMan: India?",
                posterPath = "",
                releaseDate = "2023-01-22",
                overview = "A small, wealthy family in New York City gets progressively torn apart by secrets, lies, and the theft that orchestrates all of it."
            ),
            onClick = {}
        )
    }
}
