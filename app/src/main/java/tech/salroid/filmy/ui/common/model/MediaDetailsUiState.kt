package tech.salroid.filmy.ui.common.model

import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.data.local.model.Youtube
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse

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
    val voteAverage: Double?,
    val voteCount: Long?,
    val youtubeTrailers: List<Youtube>?,
    val isFavorite: Boolean,
    val isWatchlist: Boolean,
    val isTvShow: Boolean,
    val imdbId: String? = null,
    val reviews: ReviewResponse? = null,
    val watchProviders: WatchProviderResponse? = null,
    val castAndCrew: CastAndCrewResponse? = null,
    val similarMedia: SimilarMoviesResponse? = null,
    val recommendations: SimilarMoviesResponse? = null
)