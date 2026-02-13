package tech.salroid.filmy.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun DetailsInfoItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.alpha(0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun DetailsSection(
    title: String,
    onViewAllClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (onViewAllClick != null) {
                TextButton(onClick = onViewAllClick) {
                    Text(stringResource(R.string.view_all))
                }
            }
        }
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsInfoItemPreview() {
    AppTheme {
        DetailsInfoItem(label = "Runtime", value = "120m")
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsSectionPreview() {
    AppTheme {
        DetailsSection(title = "Cast", onViewAllClick = {}) {
            Text("Sample Content")
        }
    }
}
