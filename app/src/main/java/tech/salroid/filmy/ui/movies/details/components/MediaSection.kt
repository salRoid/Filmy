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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.SimilarMovie
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun MediaSuggestionsSection(
    title: String,
    response: SimilarMoviesResponse?,
    onMediaClick: (Int) -> Unit
) {
    if (response?.results?.isNotEmpty() == true) {
        DetailsSection(title = title) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(response.results) { media ->
                    SuggestionItem(media.displayTitle, media.posterPath) {
                        onMediaClick(media.id ?: 0)
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionItem(
    title: String?,
    posterPath: String?,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = stringResource(R.string.movie_poster_url, posterPath ?: ""),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp, 145.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.poster_error_placeholder)
        )
        Text(
            text = title ?: "",
            style = MaterialTheme.typography.labelSmall,
            minLines = 2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MediaSuggestionsSectionPreview() {
    val sampleMovies = SimilarMoviesResponse(
        results = arrayListOf(
            SimilarMovie(
                id = 1,
                title = "Inception",
                posterPath = "/edv5bs1pS9v796LpT2M0sYhC76B.jpg"
            ),
            SimilarMovie(
                id = 2,
                title = "Interstellar",
                posterPath = "/edv5bs1pS9v796LpT2M0sYhC76B.jpg"
            )
        )
    )
    AppTheme {
        MediaSuggestionsSection(
            title = "Similar",
            response = sampleMovies,
            onMediaClick = {}
        )
    }
}
