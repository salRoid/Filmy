package tech.salroid.filmy.ui.common.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.window.core.layout.WindowSizeClass
import tech.salroid.filmy.ui.LocalWindowSizeClass
import tech.salroid.filmy.utility.toUserMessage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviewList(
    modifier: Modifier = Modifier,
    items: Int,
    key: ((index: Int) -> Any)? = null,
    header: (@Composable () -> Unit)? = null,
    stickyHeader: (@Composable () -> Unit)? = null,
    content: @Composable (index: Int) -> Unit
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val cells = when {
        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
        ) -> 6

        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        ) -> 4

        else -> 3
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (header != null) {
            item { header() }
        }
        if (stickyHeader != null) {
            stickyHeader { stickyHeader() }
        }

        val rows = (items + cells - 1) / cells
        items(rows) { rowIndex ->
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (columnIndex in 0 until cells) {
                    val index = rowIndex * cells + columnIndex
                    if (index < items) {
                        Box(modifier = Modifier.weight(1f)) {
                            content(index)
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T : Any> PaginatedPreviewList(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<T>,
    header: (@Composable () -> Unit)? = null,
    stickyHeader: (@Composable () -> Unit)? = null,
    content: @Composable (T) -> Unit
) {
    val windowSizeClass = LocalWindowSizeClass.current
    val cells = when {
        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
        ) -> 6

        windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        ) -> 4

        else -> 3
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (header != null) {
            item { header() }
        }
        if (stickyHeader != null) {
            stickyHeader { stickyHeader() }
        }

        val rowCount = (items.itemCount + cells - 1) / cells
        items(rowCount) { rowIndex ->
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (columnIndex in 0 until cells) {
                    val index = rowIndex * cells + columnIndex
                    if (index < items.itemCount) {
                        val item = items[index]
                        if (item != null) {
                            Box(modifier = Modifier.weight(1f)) {
                                content(item)
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            when (val state = items.loadState.append) {
                is LoadState.Loading -> {
                    LoadingWidget(modifier = Modifier.fillMaxWidth().padding(16.dp))
                }
                is LoadState.Error -> {
                    ErrorWidget(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        message = state.error.toUserMessage(),
                        onRetryClick = { items.retry() }
                    )
                }
                else -> {}
            }
        }
    }
}
