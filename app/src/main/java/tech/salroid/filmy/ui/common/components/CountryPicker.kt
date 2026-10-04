package tech.salroid.filmy.ui.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import java.util.Locale

data class CountryOption(val code: String, val name: String)

fun allCountryOptions(): List<CountryOption> = Locale.getISOCountries()
    .map { code -> CountryOption(code, Locale("", code).displayCountry) }
    .filter { it.name.isNotBlank() }
    .sortedBy { it.name }

@Composable
fun CountrySelectionList(
    selected: String,
    onSelect: (CountryOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val countries = remember { allCountryOptions() }
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) countries else countries.filter { it.name.contains(query, ignoreCase = true) }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search country") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        LazyColumn(Modifier.selectableGroup()) {
            items(filtered, key = { it.code }) { country ->
                val isSelected = country.code == selected
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .selectable(
                            selected = isSelected,
                            onClick = { onSelect(country) },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = isSelected, onClick = null)
                    Text(
                        text = country.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
