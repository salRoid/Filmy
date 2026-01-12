package tech.salroid.filmy.ui.shows.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.common.components.PreviewItem
import tech.salroid.filmy.ui.movies.dummyShowPreview

@Composable
fun ShowItem(
    modifier: Modifier = Modifier,
    show: TvShowPreview,
    onShowClick: () -> Unit
) {
    PreviewItem(
        modifier = modifier,
        title = show.title,
        posterUrl = show.posterUrl,
        readableDate = show.firstAirReadableDate,
        contentDescription = "${show.title} - Show Item",
        onItemClick = onShowClick
    )
}

@Preview(
    name = "Show Item - Preview",
    showBackground = true
)
@Composable
fun MovieItemPreview() {
    ShowItem(
        modifier = Modifier
            .width(140.dp)
            .padding(16.dp),
        show = dummyShowPreview,
        onShowClick = { }
    )
}