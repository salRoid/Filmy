package tech.salroid.filmy.ui.search.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import tech.salroid.filmy.data.model.SearchPreview
import tech.salroid.filmy.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: ImmutableList<SearchPreview>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
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
            SearchList(searchPreviews = searchResults) {
                textFieldState.edit { replace(0, length, searchResults[it].title) }
                onExpandedChange(false)
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
            searchResults = persistentListOf(),
            expanded = false,
            onExpandedChange = {}
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
            searchResults = persistentListOf(
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
            ),
            expanded = true,
            onExpandedChange = {}
        )
    }
}
