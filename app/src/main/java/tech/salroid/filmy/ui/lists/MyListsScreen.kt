package tech.salroid.filmy.ui.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.model.account.TmdbList
import tech.salroid.filmy.ui.common.components.LoadingWidget
import tech.salroid.filmy.ui.home.LoginViewModel
import tech.salroid.filmy.ui.home.rememberLoginLauncher
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun MyListsScreen(
    viewModel: MyListsViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel(),
    onListClick: (Int, String) -> Unit,
    onBackClick: () -> Unit
) {
    val lists by viewModel.lists.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val itemPreviews by viewModel.itemPreviews.collectAsStateWithLifecycle()
    val profile by loginViewModel.uiStateProfile.collectAsStateWithLifecycle()
    val canManageLists by loginViewModel.canManageLists.collectAsStateWithLifecycle()
    val startLogin = rememberLoginLauncher(loginViewModel)

    var showCreateDialog by remember { mutableStateOf(false) }
    var listToDelete by remember { mutableStateOf<TmdbList?>(null) }

    // Logging in here refreshes this same screen's lists immediately -
    // no need to leave and come back.
    LaunchedEffect(profile, canManageLists) {
        if (profile != null && canManageLists) {
            viewModel.loadLists()
        }
    }

    if (profile == null || !canManageLists) {
        LoggedOutContent(
            // Logged in, but from before lists could hold shows: one more
            // login grants the access they need.
            message = if (profile != null) {
                stringResource(R.string.lists_relogin_required_message)
            } else {
                stringResource(R.string.lists_login_required_message)
            },
            onLoginClick = startLogin,
            onBackClick = onBackClick
        )
    } else {
        MyListsContent(
            lists = lists,
            itemPreviews = itemPreviews,
            isLoading = isLoading,
            onListClick = onListClick,
            onCreateClick = { showCreateDialog = true },
            onListLongClick = { listToDelete = it },
            onBackClick = onBackClick
        )
    }

    if (showCreateDialog) {
        CreateListDialog(
            onCreate = { name ->
                viewModel.createList(name)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    listToDelete?.let { list ->
        AlertDialog(
            onDismissRequest = { listToDelete = null },
            title = {
                Text(
                    stringResource(R.string.delete_list_confirmation),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = { Text(list.name ?: "") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteList(list.id)
                    listToDelete = null
                }) {
                    Text(stringResource(R.string.remove))
                }
            },
            dismissButton = {
                TextButton(onClick = { listToDelete = null }) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MyListsContent(
    lists: List<TmdbList>,
    itemPreviews: Map<Int, List<String>>,
    isLoading: Boolean,
    onListClick: (Int, String) -> Unit,
    onCreateClick: () -> Unit,
    onListLongClick: (TmdbList) -> Unit,
    onBackClick: () -> Unit
) {
    val gridState = rememberLazyStaggeredGridState()
    // The button shows its label at rest and shrinks to just the icon once
    // the cards are scrolled, so it stays out of their way.
    val isAtTop by remember {
        derivedStateOf { gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0 }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_lists)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isLoading) {
                SmallExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.create_new_list)) },
                    // Once collapsed the label is gone, so the icon has to carry it.
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = if (isAtTop) null else stringResource(R.string.create_new_list)
                        )
                    },
                    onClick = onCreateClick,
                    expanded = isAtTop
                )
            }
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                LoadingWidget(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                )
            }

            lists.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_lists_yet),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(minSize = 160.dp),
                    state = gridState,
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    // Bottom room so the last cards can scroll clear of the button.
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalItemSpacing = 12.dp
                ) {
                    items(lists, key = { it.id }) { list ->
                        ListCard(
                            list = list,
                            previewTitles = itemPreviews[list.id],
                            onClick = { onListClick(list.id, list.name ?: "") },
                            onLongClick = { onListLongClick(list) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoggedOutContent(
    message: String,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_lists)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLoginClick) {
                Text(stringResource(R.string.login_now))
            }
        }
    }
}

/**
 * One list as a card: its name, the first few titles in it and how many it
 * holds in total. Cards differ in height with how much they have to show,
 * which is what the staggered grid is for.
 *
 * [previewTitles] is null while that list's titles are still loading.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListCard(
    list: TmdbList,
    previewTitles: List<String>?,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val itemCount = list.itemCount ?: 0
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .combinedClickable(
                onClick = onClick,
                onClickLabel = stringResource(R.string.cd_open_details),
                onLongClick = onLongClick,
                onLongClickLabel = stringResource(R.string.remove),
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {}
            .padding(16.dp)
    ) {
        Text(
            text = list.name ?: "",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        if (itemCount == 0) {
            Text(
                text = stringResource(R.string.list_empty),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
            return@Column
        }

        if (!previewTitles.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            previewTitles.forEach { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            val remaining = itemCount - previewTitles.size
            if (remaining > 0) {
                Text(
                    text = stringResource(R.string.list_more_items, remaining),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Text(
            text = pluralStringResource(R.plurals.list_item_count, itemCount, itemCount),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun MyListsContentPreview() {
    AppTheme {
        MyListsContent(
            lists = listOf(
                TmdbList(id = 1, name = "Weekend watch", itemCount = 12),
                TmdbList(id = 2, name = "Shows to finish", itemCount = 3),
                TmdbList(id = 3, name = "Rewatch someday", itemCount = 0),
                TmdbList(id = 4, name = "Christopher Nolan, ranked", itemCount = 2),
                TmdbList(id = 5, name = "Still loading", itemCount = 7),
                TmdbList(id = 6, name = "Just the one", itemCount = 1)
            ),
            itemPreviews = mapOf(
                1 to listOf("Fight Club", "Inception", "Breaking Bad", "The Grand Budapest Hotel"),
                2 to listOf("Severance", "The Bear", "Dark"),
                4 to listOf("The Prestige", "Interstellar"),
                6 to listOf("Paddington 2")
            ),
            isLoading = false,
            onListClick = { _, _ -> },
            onCreateClick = {},
            onListLongClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
internal fun MyListsReloginPreview() {
    AppTheme {
        LoggedOutContent(
            message = stringResource(R.string.lists_relogin_required_message),
            onLoginClick = {},
            onBackClick = {}
        )
    }
}
