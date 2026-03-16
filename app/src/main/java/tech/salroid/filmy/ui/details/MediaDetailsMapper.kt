package tech.salroid.filmy.ui.details

import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.ui.common.model.MediaDetailsUiState
import tech.salroid.filmy.utility.toReadableDate
import javax.inject.Inject

class MediaDetailsMapper @Inject constructor() {

    fun map(
        movie: MovieDetails?,
        tv: TvDetails?,
        reviews: ReviewResponse?,
        watchProviders: WatchProviderResponse?,
        cast: CastAndCrewResponse?,
        similar: SimilarMoviesResponse?,
        recommendations: SimilarMoviesResponse?
    ): MediaDetailsUiState? {
        if (movie != null && tv == null) {
            val hours = movie.runtime?.div(60) ?: 0
            val mins = movie.runtime?.rem(60) ?: 0
            val runtimeText = "${hours}h ${mins}m"
            val releaseDateText = movie.releaseDate?.toReadableDate()?.let { " • $it" } ?: ""

            return MediaDetailsUiState(
                mediaId = movie.id,
                title = movie.title ?: "",
                overview = movie.overview ?: "",
                tagline = movie.tagline ?: "",
                backdropPath = movie.backdropPath,
                posterPath = movie.posterPath,
                genres = movie.genres.joinToString(" / ") { it.name ?: "" },
                runtimeText = runtimeText,
                releaseDateText = releaseDateText,
                voteAverage = movie.voteAverage,
                voteCount = movie.voteCount,
                youtubeTrailers = movie.trailers?.youtube,
                isFavorite = movie.favorite,
                isWatchlist = movie.watchlist,
                isTvShow = false,
                imdbId = movie.imdbId,
                reviews = reviews,
                watchProviders = watchProviders,
                castAndCrew = cast,
                similarMedia = similar,
                recommendations = recommendations
            )
        } else if (tv != null) {
            val runtime = tv.episodeRunTime.firstOrNull() ?: 0
            val runtimeText = "${runtime}m"
            val releaseDateText = tv.firstAirDate?.toReadableDate()?.let { " • $it" } ?: ""

            return MediaDetailsUiState(
                mediaId = tv.id ?: 0,
                title = tv.name ?: "",
                overview = tv.overview ?: "",
                tagline = tv.tagline ?: "",
                backdropPath = tv.backdropPath,
                posterPath = tv.posterPath,
                genres = tv.genres.joinToString(" / ") { it.name ?: "" },
                runtimeText = runtimeText,
                releaseDateText = releaseDateText,
                voteAverage = tv.voteAverage,
                voteCount = tv.voteCount?.toLong(),
                youtubeTrailers = tv.trailers?.youtube,
                isFavorite = movie?.favorite ?: false,
                isWatchlist = movie?.watchlist ?: false,
                isTvShow = true,
                imdbId = null,
                reviews = reviews,
                watchProviders = watchProviders,
                castAndCrew = cast,
                similarMedia = similar,
                recommendations = recommendations
            )
        }
        return null
    }
}
