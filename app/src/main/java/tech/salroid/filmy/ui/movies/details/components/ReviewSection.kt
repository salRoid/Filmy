package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.model.ReviewResponseUiModel
import tech.salroid.filmy.ui.common.model.ReviewUiModel
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun ReviewsSection(
    reviews: ReviewResponseUiModel?,
    onViewAllClick: (() -> Unit)? = null,
    onReviewClick: (String, String) -> Unit
) {
    if (reviews?.results?.isNotEmpty() == true) {
        val showViewAll = reviews.results.size > 2
        
        DetailsSection(
            title = "Reviews"
        ) {
            Column {
                reviews.results.take(2).forEach { review ->
                    ReviewItem(review) {
                        onReviewClick(review.author, review.content)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                if (showViewAll && onViewAllClick != null) {
                    SeeAllItem(onClick = onViewAllClick)
                }
            }
        }
    }
}

@Composable
fun ReviewItem(
    review: ReviewUiModel,
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
                    model = review.authorAvatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(R.drawable.default_avatar),
                    error = painterResource(R.drawable.default_avatar)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = review.author, style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = review.createdAt,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.alpha(0.6f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.content,
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
    val sampleReviews = ReviewResponseUiModel(
        results = listOf(
            ReviewUiModel(id = "1", author = "John Doe", content = "Great movie!", createdAt = "01 Jan 2024", authorAvatarUrl = null),
            ReviewUiModel(id = "2", author = "Jane Doe", content = "I loved it!", createdAt = "02 Jan 2024", authorAvatarUrl = null)
        )
    )
    AppTheme {
        ReviewsSection(
            reviews = sampleReviews,
            onReviewClick = { _, _ -> })
    }
}
