package tech.salroid.filmy.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.ui.screens.dummyMovie
import tech.salroid.filmy.utility.toReadableDate

@Composable
fun MovieItem(
    modifier: Modifier,
    movie: Movie
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(corner = CornerSize(4.dp)),
            colors = CardDefaults.cardColors(containerColor = Transparent),
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(corner = CornerSize(4.dp))),
                contentScale = ContentScale.FillBounds,
                model = stringResource(R.string.movie_poster_url, movie.posterPath.orEmpty()),
                contentDescription = "${movie.title} movie poster"
            )
        }
        Text(
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 0.0.sp),
            text = movie.title.orEmpty()
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            text = movie.releaseDate?.toReadableDate().orEmpty()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MovieItemPreview() {
    MovieItem(
        modifier = Modifier,
        movie = dummyMovie
    )
}