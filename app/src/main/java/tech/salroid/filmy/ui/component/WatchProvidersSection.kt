package tech.salroid.filmy.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun WatchProvidersSection(watchProviders: WatchProviderResponse?) {
    watchProviders?.let { providers ->
        val stream = providers.results?.IN?.flatrate?.firstOrNull()
        val buy = providers.results?.IN?.buy?.firstOrNull()
        val rent = providers.results?.IN?.rent?.firstOrNull()

        val logoPath = stream?.logoPath ?: buy?.logoPath ?: rent?.logoPath
        val providerName = stream?.providerName ?: buy?.providerName ?: rent?.providerName

        if (logoPath != null && providerName != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = stringResource(R.string.member_profile_url, logoPath),
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.now_streaming),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = stringResource(R.string.watch_now),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WatchProvidersSectionPreview() {
    AppTheme {
        // Mock data would be complex here, just verifying it handles null
        WatchProvidersSection(watchProviders = null)
    }
}
