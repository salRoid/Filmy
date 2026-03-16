package tech.salroid.filmy.ui.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tech.salroid.filmy.ui.search.SearchScreenState
import tech.salroid.filmy.ui.search.component.AppSearchBar

@Composable
fun HomeTopBar(
    textFieldState: TextFieldState,
    searchUiState: SearchScreenState,
    isSearchExpanded: Boolean,
    onSearchExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit
) {
    Column(modifier = Modifier.statusBarsPadding()) {
        AnimatedVisibility(
            visible = !isSearchExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            AppBranding()
        }

        AppSearchBar(
            textFieldState = textFieldState,
            onSearch = onSearch,
            searchResults = when (searchUiState) {
                is SearchScreenState.Success -> searchUiState.previews
                else -> emptyList()
            },
            expanded = isSearchExpanded,
            onExpandedChange = onSearchExpandedChange
        )
    }
}
