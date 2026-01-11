package tech.salroid.filmy.ui.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ErrorWidget(
    modifier: Modifier,
    message: String,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            message,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onRetryClick
        ) {
            Text(
                "RETRY",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ErrorWidgetPreview() {
    ErrorWidget(
        modifier = Modifier,
        "Something went wrong! Check your internet connection also.",
        onRetryClick = {}
    )
}