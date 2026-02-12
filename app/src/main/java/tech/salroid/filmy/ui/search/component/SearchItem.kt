package tech.salroid.filmy.ui.search.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import tech.salroid.filmy.data.model.SearchPreview
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun SearchItem(
    modifier: Modifier = Modifier,
    searchPreview: SearchPreview,
    onItemClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(corner = CornerSize(4.dp)),
        colors = CardDefaults.cardColors(containerColor = Transparent),
        onClick = { onItemClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                modifier = Modifier
                    .height(80.dp)
                    .width(50.dp)
                    .clip(RoundedCornerShape(corner = CornerSize(4.dp))),
                contentScale = ContentScale.Crop,
                model = searchPreview.posterUrl,
                contentDescription = "${searchPreview.title} Poster Image"
            )

            Spacer(modifier = Modifier.padding(start = 16.dp))

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        letterSpacing = 0.0.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    ),
                    text = searchPreview.title
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    text = searchPreview.readableReleaseDate
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 16.dp),
            color = Color.LightGray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchItemPreview() {
    AppTheme {
        SearchItem(
            searchPreview = SearchPreview(
                id = 1,
                title = "Inception",
                posterUrl = "",
                readableReleaseDate = "2010"
            ),
            onItemClick = {}
        )
    }
}
