package tech.salroid.filmy.ui.common.model

import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.data.local.model.Youtube

data class MediaDetailsUiState(
    val mediaId: Int,
    val title: String,
    val overview: String,
    val tagline: String,
    val backdropPath: String?,
    val posterPath: String?,
    val genres: String,
    val runtimeText: String,
    val releaseDateText: String,
    val youtubeTrailers: List<Youtube>?,
    val isWatched: Boolean,
    val isWatchlist: Boolean,
    val isTvShow: Boolean,
    val imdbId: String? = null,
    val reviews: ReviewResponseUiModel? = null,
    val watchProviders: WatchProvidersUiModel? = null,
    val castAndCrew: CastAndCrewResponse? = null,
    val similarMedia: SimilarMoviesResponse? = null,
    val recommendations: SimilarMoviesResponse? = null,
    val ratings: RatingsUiModel? = null
)
