package tech.salroid.filmy.ui.search.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import tech.salroid.filmy.data.model.SearchPreview
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
    modifier: Modifier = Modifier
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
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = {
                        onSearch(textFieldState.text.toString())
                        onExpandedChange(false)
                    },
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    placeholder = {
                        Text(
                            modifier = Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            text = "Search Movies or Shows"
                        )
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

            val searchResults = (searchUiState as? SearchScreenState.Success)?.previews ?: emptyList()
            SearchList(searchPreviews = searchResults) { item ->
                onExpandedChange(false)
                onSearchResultClick(item)
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
