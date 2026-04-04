package tech.salroid.filmy.ui.shows.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.common.components.PaginatedPreviewList

@Composable
fun ShowsList(
    modifier: Modifier = Modifier,
    shows: LazyPagingItems<TvShowPreview>,
    onShowClick: (Int) -> Unit
) {
    PaginatedPreviewList(
        modifier = modifier,
        items = shows,
        content = { show ->
            ShowItem(show = show, onShowClick = {
                onShowClick(show.id)
            })
        }
    )
}
