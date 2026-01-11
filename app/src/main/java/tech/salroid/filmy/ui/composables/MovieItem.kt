package tech.salroid.filmy.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.movies.dummyMoviePreview

@Composable
fun MovieItem(
    modifier: Modifier,
    movie: MoviePreview,
    onMovieClick: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(corner = CornerSize(4.dp)),
        colors = CardDefaults.cardColors(containerColor = Transparent),
        onClick = { onMovieClick(movie.id) }
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(corner = CornerSize(4.dp))),
                contentScale = ContentScale.Crop,
                model = movie.posterUrl,
                contentDescription = "${movie.title} movie poster"
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall.copy(
                    letterSpacing = 0.0.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                ),
                text = movie.title
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                text = movie.readableReleaseDate
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MovieItemPreview() {
    MovieItem(
        modifier = Modifier
            .width(140.dp)
            .padding(16.dp),
        movie = dummyMoviePreview,
        onMovieClick = { }
    )
}