package tech.salroid.filmy.ui.search.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import tech.salroid.filmy.data.model.SearchPreview
import tech.salroid.filmy.ui.common.components.ErrorWidget
import tech.salroid.filmy.ui.search.SearchScreenState
import tech.salroid.filmy.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchUiState: SearchScreenState,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSearchResultClick: (SearchPreview) -> Unit,
    modifier: Modifier = Modifier,
    recentSearches: List<String> = emptyList(),
    onRecentSearchClick: (String) -> Unit = {},
    onRemoveRecentSearch: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {}
) {
    Box(
        modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }
            .offset(y = (-8).dp)
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .semantics { traversalIndex = 0f }
                .padding(
                    start = if (!expanded) 12.dp else 0.dp,
                    end = if (!expanded) 12.dp else 0.dp,
                    top = 0.dp,
                    bottom = if (!expanded) 4.dp else 0.dp
                ),
            inputField = {
                SearchBarDefaults.InputField(
                    state = textFieldState,
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                    },
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    placeholder = {
                        if (expanded) {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                text = "Search Movies, Shows or People"
                            )
                        } else {
                            RotatingSearchPlaceholder(modifier = Modifier.padding(start = 16.dp))
                        }
                    },
                    trailingIcon = {
                        if (textFieldState.text.isNotEmpty()) {
                            IconButton(onClick = { textFieldState.clearText() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = onExpandedChange,
        ) {
            var showLoading by remember { mutableStateOf(false) }
            LaunchedEffect(searchUiState) {
                if (searchUiState is SearchScreenState.Loading) {
                    delay(400) // Show loading only if it takes more than 400ms
                    showLoading = true
                } else {
                    showLoading = false
                }
            }

            AnimatedVisibility(
                visible = showLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            val query = textFieldState.text.toString()
            when {
                searchUiState is SearchScreenState.Error -> {
                    ErrorWidget(
                        modifier = Modifier.fillMaxSize(),
                        message = searchUiState.errorMessage,
                        onRetryClick = { onSearch(query) }
                    )
                }

                query.isBlank() -> {
                    RecentSearchesList(
                        recentSearches = recentSearches,
                        onRecentSearchClick = { recent ->
                            textFieldState.setTextAndPlaceCursorAtEnd(recent)
                            onSearch(recent)
                        },
                        onRemoveClick = onRemoveRecentSearch,
                        onClearAllClick = onClearRecentSearches
                    )
                }

                searchUiState is SearchScreenState.Success && searchUiState.previews.isEmpty() -> {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        text = "No results for \"$query\"",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                else -> {
                    val searchResults = (searchUiState as? SearchScreenState.Success)?.previews ?: emptyList()
                    SearchList(searchPreviews = searchResults) { item ->
                        onExpandedChange(false)
                        onSearchResultClick(item)
                    }
                }
            }
        }
    }
}

private val searchPlaceholderSubjects = listOf("Movies", "Shows", "People")

@Composable
private fun RotatingSearchPlaceholder(modifier: Modifier = Modifier) {
    var index by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4800)
            index = (index + 1) % searchPlaceholderSubjects.size
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Search ",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
        AnimatedContent(
            targetState = index,
            modifier = Modifier.clipToBounds(),
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn()) togetherWith
                    (slideOutVertically { height -> -height } + fadeOut())
            },
            label = "search_placeholder_subject"
        ) { subjectIndex ->
            Text(
                text = searchPlaceholderSubjects[subjectIndex],
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RecentSearchesList(
    recentSearches: List<String>,
    onRecentSearchClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit,
    onClearAllClick: () -> Unit
) {
    if (recentSearches.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent searches",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onClearAllClick) {
                Text(text = "Clear all", style = MaterialTheme.typography.labelMedium)
            }
        }
        LazyColumn {
            items(recentSearches, key = { it }) { recent ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRecentSearchClick(recent) }
                        .padding(horizontal = 22.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.height(18.dp)
                        )
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            text = recent,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    IconButton(onClick = { onRemoveClick(recent) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.height(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppSearchBarPreview() {
    AppTheme {
        AppSearchBar(
            textFieldState = rememberTextFieldState(),
            onSearch = {},
            searchUiState = SearchScreenState.Idle,
            expanded = false,
            onExpandedChange = {},
            onSearchResultClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppSearchBarExpandedPreview() {
    AppTheme {
        AppSearchBar(
            textFieldState = rememberTextFieldState("Inception"),
            onSearch = {},
            searchUiState = SearchScreenState.Success(listOf(
                SearchPreview(
                    1,
                    "Inception",
                    "",
                    "2010"
                ),
                SearchPreview(
                    2,
                    "Interstellar",
                    "",
                    "2014"
                )
            )),
            expanded = true,
            onExpandedChange = {},
            onSearchResultClick = {}
        )
    }
}
