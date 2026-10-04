package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.account.TmdbList
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.lists.CreateListDialog
import tech.salroid.filmy.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToListSheet(
    lists: List<TmdbList>,
    membership: Map<Int, Boolean>,
    isLoading: Boolean,
    onToggle: (listId: Int, currentlyIn: Boolean) -> Unit,
    onCreateList: (name: String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        AddToListContent(
            lists = lists,
            membership = membership,
            isLoading = isLoading,
            onToggle = onToggle,
            onCreateList = onCreateList
        )
    }
}

/**
 * The sheet's body: the user's lists as rows that toggle this title in or out
 * of each, and a button to make a new list.
 *
 * [membership] has no entry for a list until TMDB has said whether the title
 * is in it; that row shows a small spinner and can't be toggled until then.
 */
@Composable
internal fun AddToListContent(
    lists: List<TmdbList>,
    membership: Map<Int, Boolean>,
    isLoading: Boolean,
    onToggle: (listId: Int, currentlyIn: Boolean) -> Unit,
    onCreateList: (name: String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

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

        when {
            isLoading -> {
                val loadingLabel = stringResource(R.string.loading_lists)
                LoadingWidget(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .semantics { contentDescription = loadingLabel }
                )
            }

            lists.isEmpty() -> {
                Text(
                    text = stringResource(R.string.no_lists_yet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            else -> {
                // Capped so a long collection of lists scrolls inside the
                // sheet and the create button stays in reach.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lists, key = { it.id }) { list ->
                        val isMember = membership[list.id]
                        ListToggleRow(
                            list = list,
                            isMember = isMember,
                            onToggle = { onToggle(list.id, isMember == true) }
                        )
                    }
                }
            }
        }

        FilledTonalButton(
            onClick = { showCreateDialog = true },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.create_new_list))
        }
    }

    if (showCreateDialog) {
        CreateListDialog(
            onCreate = { name ->
                onCreateList(name)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }
}

/** [isMember] is null while that list's membership is still being checked. */
@Composable
private fun ListToggleRow(
    list: TmdbList,
    isMember: Boolean?,
    onToggle: () -> Unit
) {
    val selected = isMember == true
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        label = "list_row_color"
    )
    val itemCount = list.itemCount ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .toggleable(
                value = selected,
                enabled = isMember != null,
                role = Role.Checkbox,
                onValueChange = { onToggle() }
            )
            .semantics(mergeDescendants = true) {}
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = list.name ?: "",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pluralStringResource(R.plurals.list_item_count, itemCount, itemCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
            Crossfade(targetState = isMember, label = "list_row_state") { state ->
                when (state) {
                    null -> CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )

                    true -> Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    false -> Icon(
                        Icons.Outlined.AddCircleOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun AddToListContentPreview() {
    AppTheme {
        AddToListContent(
            lists = listOf(
                TmdbList(id = 1, name = "Weekend watch", itemCount = 12),
                TmdbList(id = 2, name = "Shows to finish", itemCount = 1),
                TmdbList(id = 3, name = "Rewatch someday", itemCount = 0)
            ),
            // The third list's membership hasn't come back yet.
            membership = mapOf(1 to true, 2 to false),
            isLoading = false,
            onToggle = { _, _ -> },
            onCreateList = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun AddToListLoadingPreview() {
    AppTheme {
        AddToListContent(
            lists = emptyList(),
            membership = emptyMap(),
            isLoading = true,
            onToggle = { _, _ -> },
            onCreateList = {}
        )
    }
}
