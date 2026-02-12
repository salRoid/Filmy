package tech.salroid.filmy.ui.common.components

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.salroid.filmy.ui.movies.dummyShowPreview

@Composable
fun PreviewItem(
    modifier: Modifier = Modifier,
    title: String,
    posterUrl: String,
    readableDate: String,
    contentDescription: String? = null,
    onItemClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Transparent),
        onClick = onItemClick
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(corner = CornerSize(8.dp))),
                contentScale = ContentScale.Crop,
                model = posterUrl,
                contentDescription = contentDescription
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                text = title
            )
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(0.8f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                text = readableDate
            )
        }
    }
}

@Preview(
    name = "PreviewItem - Preview",
    showBackground = true
)
@Composable
fun MovieItemPreview() {
    PreviewItem(
        modifier = Modifier
            .width(140.dp)
            .padding(16.dp),
        title = dummyShowPreview.title,
        posterUrl = dummyShowPreview.posterUrl,
        readableDate = dummyShowPreview.firstAirReadableDate,
        onItemClick = { }
    )
}