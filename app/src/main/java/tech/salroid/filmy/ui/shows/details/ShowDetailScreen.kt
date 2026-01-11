package tech.salroid.filmy.ui.shows.details

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// Improvement - Route can be created then screen can be used in that route.
@Composable
fun ShowDetailsScreen(
    showId: Int,
    modifier: Modifier = Modifier,
    onBackNavigation: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("$showId", style = MaterialTheme.typography.headlineMedium)
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Will load the show details here...",
            textAlign = TextAlign.Center
        )
        Button(onClick = onBackNavigation) {
            Text("Go Back")
        }
    }
}


@Composable
@Preview(showBackground = true)
fun MovieDetailsPreview() {
    ShowDetailsScreen(
        2414,
        modifier = Modifier,
        onBackNavigation = { }
    )
}
