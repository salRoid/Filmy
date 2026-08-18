package tech.salroid.filmy.ui.common.model

import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.Keyword
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
    val ratings: RatingsUiModel? = null,
    val certification: String? = null,
    val collectionId: Int? = null,
    val collectionName: String? = null,
    val seasons: List<SeasonUiModel>? = null,
    val userRating: Float? = null,
    val awards: String? = null,
    val budget: Long? = null,
    val revenue: Long? = null,
    val studios: List<StudioUiModel>? = null,
    val keywords: List<Keyword>? = null,
    val backdropImages: List<String>? = null,
    val homepage: String? = null,
    val facebookId: String? = null,
    val instagramId: String? = null,
    val twitterId: String? = null
)
