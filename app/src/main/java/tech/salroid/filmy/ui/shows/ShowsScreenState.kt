package tech.salroid.filmy.ui.shows

import tech.salroid.filmy.data.model.TvShowPreview

sealed class ShowsScreenState {
    object Loading : ShowsScreenState()
    data class Success(val shows: List<TvShowPreview>) : ShowsScreenState()
    data class Error(val errorMessage: String) : ShowsScreenState()
}