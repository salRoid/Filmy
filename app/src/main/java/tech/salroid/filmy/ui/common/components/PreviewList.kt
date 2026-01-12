package tech.salroid.filmy.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import tech.salroid.filmy.ui.LocalWindowSizeClass

@Composable
fun PreviewList(
    modifier: Modifier = Modifier,
    items: Int,
    key: ((index: Int) -> Any)? = null,
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

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(cells),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items, key = key) { index ->
            content(index)
        }
    }
}