package tech.salroid.filmy.ui.search

import tech.salroid.filmy.data.model.SearchPreview

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    object Idle : SearchScreenState()
    data class Success(val previews: List<SearchPreview>) : SearchScreenState()
    data class Error(val errorMessage: String) : SearchScreenState()
}