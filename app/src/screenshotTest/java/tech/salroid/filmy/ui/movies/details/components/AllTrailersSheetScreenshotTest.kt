package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import tech.salroid.filmy.data.local.model.Youtube
import tech.salroid.filmy.ui.theme.AppTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
fun TrailerItemScreenshotTest() {
    TrailerItemPreview()
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewTest
@Preview(showBackground = true)
@Composable
fun AllTrailersSheetScreenshotTest() {
    val sampleTrailers = listOf(
        Youtube(name = "Official Trailer 1", source = "8hP9D6kZseM"),
        Youtube(name = "Official Trailer 2", source = "YoHD9XEInc0"),
        Youtube(name = "Teaser Trailer", source = "8Z99vYmda99uSHI6fSToMvSztpZ")
    )
    val sheetState = remember {
        SheetState(true, { 56f }, { 125f }, SheetValue.Expanded, { true }, false)
    }
    AppTheme {
        AllTrailersSheet(
            title = "Inception",
            trailers = sampleTrailers,
            onTrailerClick = {},
            onDismiss = {},
            sheetState = sheetState
        )
    }
}
