package tech.salroid.filmy.ui.home

sealed class SearchViewUiState {
    object Hidden : SearchViewUiState()
    object Visible : SearchViewUiState()
}
