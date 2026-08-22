package tech.salroid.filmy.ui.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.model.SearchPreview
import tech.salroid.filmy.ui.search.SearchScreenState
import tech.salroid.filmy.ui.search.component.AppSearchBar

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onSearchResultClick: (SearchPreview) -> Unit,
    recentSearches: List<String> = emptyList(),
    onRecentSearchClick: (String) -> Unit = {},
    onRemoveRecentSearch: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    trailingContent: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {}
) {
    var brandingHeightPx by remember { mutableFloatStateOf(0f) }
    val collapseOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    // Collapses/expands the branding row by consuming scroll delta before the
    // list below sees it, so both animate together smoothly under the
    // finger - scrolling down tucks it away, scrolling up brings it right
    // back, while the search bar itself always stays pinned in place.
    val nestedScrollConnection = remember(isSearchExpanded) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isSearchExpanded) return Offset.Zero

                val heightOffsetLimit = -brandingHeightPx
                val newOffset = (collapseOffset.value + available.y).coerceIn(heightOffsetLimit, 0f)
                val consumed = newOffset - collapseOffset.value
                if (consumed != 0f) {
                    coroutineScope.launch { collapseOffset.snapTo(newOffset) }
                }
                return Offset(0f, consumed)
            }
        }
    }

    // Search taking over the screen already replaces the branding row's
    // purpose - animate it fully out of the way while expanded, and restore
    // it when collapsing back, matching the previous non-scroll behavior.
    LaunchedEffect(isSearchExpanded, brandingHeightPx) {
        collapseOffset.animateTo(if (isSearchExpanded) -brandingHeightPx else 0f)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .nestedScroll(nestedScrollConnection)
    ) {
        val collapsedHeightPx = if (brandingHeightPx == 0f) {
            Float.MAX_VALUE
        } else {
            (brandingHeightPx + collapseOffset.value).coerceAtLeast(0f)
        }

        CollapsibleHeader(
            collapsedHeightPx = collapsedHeightPx,
            offsetPx = collapseOffset.value,
            onNaturalHeightMeasured = { brandingHeightPx = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            AppBranding(trailingContent = trailingContent)
        }

        AppSearchBar(
            textFieldState = textFieldState,
            onSearch = onSearch,
            searchUiState = searchUiState,
            expanded = isSearchExpanded,
            onExpandedChange = onSearchExpandedChange,
            onSearchResultClick = onSearchResultClick,
            recentSearches = recentSearches,
            onRecentSearchClick = onRecentSearchClick,
            onRemoveRecentSearch = onRemoveRecentSearch,
            onClearRecentSearches = onClearRecentSearches
        )

        content()
    }
}

/**
 * Lays [content] out at its true, unconstrained natural height on every
 * measurement pass - regardless of [collapsedHeightPx] - so that height
 * never becomes a moving target as it shrinks. Only the space this layout
 * itself reports to its parent (and thus reserves for siblings below it,
 * like the search bar) follows [collapsedHeightPx]; [content] is placed at
 * [offsetPx] and clipped to whatever of it still falls within that shrunk
 * region, so it visually slides up and out as it collapses.
 */
@Composable
private fun CollapsibleHeader(
    collapsedHeightPx: Float,
    offsetPx: Float,
    onNaturalHeightMeasured: (Float) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier.clipToBounds()
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(
            constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity)
        )
        if (placeable.height.toFloat() != 0f) {
            onNaturalHeightMeasured(placeable.height.toFloat())
        }
        val layoutHeight = collapsedHeightPx.toInt().coerceIn(0, placeable.height)
        layout(placeable.width, layoutHeight) {
            placeable.placeRelative(0, offsetPx.toInt())
        }
    }
}
