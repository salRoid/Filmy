package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import tech.salroid.filmy.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@PreviewTest
@Preview(showBackground = true)
@Composable
fun FullReadSheetScreenshotTest() {
    val sheetState = remember {
        SheetState(true, { 56f }, { 125f }, SheetValue.Expanded, { true }, false)
    }
    AppTheme {
        FullReadSheet(
            title = "Inception",
            content = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster.",
            onDismiss = {},
            sheetState = sheetState
        )
    }
}
