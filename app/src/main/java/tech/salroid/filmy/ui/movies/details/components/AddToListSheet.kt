package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.account.TmdbList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToListSheet(
    lists: List<TmdbList>,
    membership: Map<Int, Boolean>,
    onToggle: (listId: Int, currentlyIn: Boolean) -> Unit,
    onCreateList: (name: String) -> Unit,
    onDismiss: () -> Unit
) {
    var newListName by remember { mutableStateOf("") }
    var showCreateField by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.add_to_list),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (lists.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_lists_yet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    lists.forEach { list ->
                        val checked = membership[list.id] ?: false
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggle(list.id, checked) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { onToggle(list.id, checked) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = list.name ?: "",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (showCreateField) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newListName,
                        onValueChange = { newListName = it },
                        placeholder = { Text(stringResource(R.string.list_name_hint)) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            if (newListName.isNotBlank()) {
                                onCreateList(newListName.trim())
                                newListName = ""
                                showCreateField = false
                            }
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.create_new_list))
                    }
                }
            } else {
                TextButton(
                    onClick = { showCreateField = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Text("+ " + stringResource(R.string.create_new_list))
                }
            }
        }
    }
}
