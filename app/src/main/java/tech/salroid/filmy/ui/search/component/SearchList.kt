package tech.salroid.filmy.ui.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import tech.salroid.filmy.data.model.SearchPreview

@Composable
fun SearchList(
    modifier: Modifier = Modifier,
    searchPreviews: ImmutableList<SearchPreview>,
    onSearchItemClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(searchPreviews, key = { it.id }) { item ->
            SearchItem(
                searchPreview = item,
                onItemClick = { onSearchItemClick(item.id) }
            )
        }
    }
}
