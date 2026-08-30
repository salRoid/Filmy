package tech.salroid.filmy.ui.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun <T> CategorySelector(
    categories: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    startExpanded: Boolean = false
) {
    var expanded by remember { mutableStateOf(startExpanded) }

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { expanded = true }
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label(selected),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(label(category)) },
                    onClick = {
                        onSelected(category)
                        expanded = false
                    },
                    trailingIcon = {
                        if (category == selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CategorySelectorPreview() {
    AppTheme {
        CategorySelector(
            categories = Movie.MovieType.entries,
            selected = Movie.MovieType.POPULAR,
            label = {
                it.name
                    .lowercase()
                    .replaceFirstChar(Char::uppercase)
                    .replace('_', ' ')
            },
            onSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategorySelectorExpandedPreview() {
    AppTheme {
        CategorySelector(
            categories = Movie.MovieType.entries,
            selected = Movie.MovieType.POPULAR,
            label = {
                it.name
                    .lowercase()
                    .replaceFirstChar(Char::uppercase)
                    .replace('_', ' ')
            },
            onSelected = {},
            startExpanded = true
        )
    }
}
