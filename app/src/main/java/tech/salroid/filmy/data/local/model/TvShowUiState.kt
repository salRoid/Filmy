package tech.salroid.filmy.data.local.model

sealed class TvShowUiState {
    object Loading : TvShowUiState()

    data class Success(
        val showResponse: TvShowResponse,
        val showType: TvShow.ShowType
    ) : TvShowUiState()

    data class Error(
        val exception: Throwable,
        val showType: TvShow.ShowType
    ) : TvShowUiState()
}