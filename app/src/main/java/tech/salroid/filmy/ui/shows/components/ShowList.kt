package tech.salroid.filmy.ui.shows.components

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.LocalWindowSizeClass
import tech.salroid.filmy.ui.common.components.PreviewList
import tech.salroid.filmy.ui.movies.dummyShowPreview

@Composable
fun ShowsList(
    modifier: Modifier = Modifier,
    shows: ImmutableList<TvShowPreview>,
    onShowClick: (Int) -> Unit
) {
    PreviewList(
        modifier = modifier,
        items = shows.size,
        key = { index ->
            shows[index].id
        },
        content = { index ->
            ShowItem(show = shows[index], onShowClick = {
                onShowClick(index)
            })
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MoviesListPreview() {
    val windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo(
        supportLargeAndXLargeWidth = true
    ).windowSizeClass

    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        ShowsList(
            modifier = Modifier,
            shows = (1..10).map { id ->
                dummyShowPreview.copy(id = id)
            }.toImmutableList(),
            onShowClick = { }
        )
    }
}