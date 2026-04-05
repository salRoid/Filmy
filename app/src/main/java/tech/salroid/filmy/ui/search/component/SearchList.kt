package tech.salroid.filmy.ui.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.data.model.SearchPreview
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun SearchList(
    modifier: Modifier = Modifier,
    searchPreviews: List<SearchPreview>,
    onSearchItemClick: (SearchPreview) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(searchPreviews, key = { it.id }) { item ->
            SearchItem(
                searchPreview = item,
                onItemClick = { onSearchItemClick(item) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchListPreview() {
    AppTheme {
        SearchList(
            searchPreviews = listOf(
                SearchPreview(
                    1,
                    "Inception",
                    "",
                    "2010"
                ),
                SearchPreview(
                    2,
                    "Interstellar",
                    "",
                    "2014"
                ),
                SearchPreview(
                    3,
                    "The Dark Knight",
                    "",
                    "2008"
                )
            ),
            onSearchItemClick = {}
        )
    }
}
