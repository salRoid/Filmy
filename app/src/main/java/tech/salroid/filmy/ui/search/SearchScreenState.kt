package tech.salroid.filmy.ui.search

import kotlinx.collections.immutable.ImmutableList
import tech.salroid.filmy.data.model.SearchPreview

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    object Idle : SearchScreenState()
    data class Success(val previews: ImmutableList<SearchPreview>) : SearchScreenState()
    data class Error(val errorMessage: String) : SearchScreenState()
}