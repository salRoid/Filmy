package tech.salroid.filmy.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(showBackground = true)
@Composable
fun AccountScreenScreenshotTest() {
    AccountScreenPreview()
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun AccountScreenLoadingScreenshotTest() {
    AccountScreenLoadingPreview()
}

@PreviewTest
@Preview(showBackground = true)
@Composable
fun AccountScreenLoggedOutScreenshotTest() {
    AccountScreenLoggedOutPreview()
}
