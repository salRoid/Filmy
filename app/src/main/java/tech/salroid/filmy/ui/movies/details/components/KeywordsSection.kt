package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.data.local.model.Keyword
import tech.salroid.filmy.ui.theme.AppTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeywordsSection(
    keywords: List<Keyword>?,
    onKeywordClick: (Int, String) -> Unit
) {
    if (keywords.isNullOrEmpty()) return

    LazyHorizontalStaggeredGrid(
        rows = StaggeredGridCells.Fixed(2),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .height(72.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalItemSpacing = 6.dp
    ) {
        items(keywords) { keyword ->
            val name = keyword.name ?: return@items
            AssistChip(
                onClick = { onKeywordClick(keyword.id, name) },
                label = {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.height(28.dp),
                shape = RoundedCornerShape(percent = 50),
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun KeywordsSectionPreview() {
    AppTheme {
        KeywordsSection(
            keywords = listOf(
                Keyword(1, "dream"), Keyword(2, "heist"), Keyword(3, "subconscious"),
                Keyword(4, "paris, france"), Keyword(5, "spy"), Keyword(6, "mind-bending")
            ),
            onKeywordClick = { _, _ -> }
        )
    }
}
