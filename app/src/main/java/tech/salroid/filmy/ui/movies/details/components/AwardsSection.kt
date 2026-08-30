package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AwardsSection(awards: String?) {
    if (awards.isNullOrBlank()) return

    DetailsInfoItem(
        label = "Awards",
        value = awards,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}
