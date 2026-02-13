package tech.salroid.filmy.ui.details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.Review
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.toReadableDate

@Composable
fun ReviewsSection(
    reviews: ReviewResponse?,
    onReviewClick: (String, String) -> Unit
) {
    if (reviews?.results?.isNotEmpty() == true) {
        DetailsSection(title = "Reviews") {
            reviews.results.take(2).forEach { review ->
                ReviewItem(review) {
                    onReviewClick(review.author ?: "", review.content ?: "")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: Review,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = review.authorDetails?.getAvatarUrl(LocalContext.current),
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(R.drawable.default_avatar),
                    error = painterResource(R.drawable.default_avatar)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = review.author ?: "", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = review.createdAt?.toReadableDate() ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.alpha(0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.content ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewsSectionPreview() {
    val sampleReviews = ReviewResponse(
        results = arrayListOf(
            Review(author = "John Doe", content = "Great movie!"),
            Review(author = "Jane Doe", content = "I loved it!")
        )
    )
    AppTheme {
        ReviewsSection(
            reviews = sampleReviews,
            onReviewClick = { _, _ -> })
    }
}
