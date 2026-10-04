package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R

@Composable
fun AwardsSection(awards: String?) {
    if (awards.isNullOrBlank()) return

    DetailsInfoItem(
        label = stringResource(R.string.awards),
        value = awards,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}
