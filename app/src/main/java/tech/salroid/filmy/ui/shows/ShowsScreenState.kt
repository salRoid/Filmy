package tech.salroid.filmy.ui.shows

import kotlinx.collections.immutable.ImmutableList
import tech.salroid.filmy.data.model.TvShowPreview

sealed class ShowsScreenState {
    object Loading : ShowsScreenState()
    data class Success(val shows: ImmutableList<TvShowPreview>) : ShowsScreenState()
    data class Error(val errorMessage: String) : ShowsScreenState()
}