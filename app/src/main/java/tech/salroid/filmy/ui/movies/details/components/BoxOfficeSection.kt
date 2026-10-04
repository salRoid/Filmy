package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R
import tech.salroid.filmy.utility.toMoneyString

@Composable
fun BoxOfficeSection(budget: Long?, revenue: Long?) {
    if (budget == null && revenue == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        budget?.let {
            DetailsInfoItem(
                label = stringResource(R.string.budget),
                value = it.toMoneyString(),
                modifier = Modifier.weight(1f)
            )
        }
        revenue?.let {
            DetailsInfoItem(
                label = stringResource(R.string.revenue),
                value = it.toMoneyString(),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
