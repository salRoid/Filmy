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
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.data.local.model.Keyword

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
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
