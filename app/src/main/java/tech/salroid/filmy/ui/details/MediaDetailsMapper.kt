package tech.salroid.filmy.ui.details

import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.RatingResponse
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.data.local.model.Videos
import tech.salroid.filmy.data.local.model.Youtube
import tech.salroid.filmy.ui.common.model.*
import tech.salroid.filmy.utility.parseHtml
import tech.salroid.filmy.utility.toReadableDate
import java.util.Locale
import javax.inject.Inject

class MediaDetailsMapper @Inject constructor() {

    fun map(
        movie: MovieDetails?,
        tv: TvDetails?,
        reviews: ReviewResponse?,
        watchProviders: WatchProviderResponse?,
        cast: CastAndCrewResponse?,
        similar: SimilarMoviesResponse?,
        recommendations: SimilarMoviesResponse?,
        ratings: RatingResponse?
    ): MediaDetailsUiState? {
        val providersUiModel = mapWatchProviders(watchProviders)
        val reviewsUiModel = mapReviews(reviews)
        val ratingsUiModel = mapRatings(movie, tv, ratings)

        if (movie != null && tv == null) {
            val hours = movie.runtime?.div(60) ?: 0
            val mins = movie.runtime?.rem(60) ?: 0
            val runtimeText = if (hours > 0 || mins > 0) "${hours}h ${mins}m" else ""
            val releaseDate = movie.releaseDate?.toReadableDate() ?: ""
            val releaseDateText = if (runtimeText.isNotEmpty() && releaseDate.isNotEmpty()) " • $releaseDate" else releaseDate

            return MediaDetailsUiState(
                mediaId = movie.id,
                title = movie.title ?: "",
                overview = (movie.overview ?: "").parseHtml(),
                tagline = movie.tagline ?: "",
                backdropPath = movie.backdropPath,
                posterPath = movie.posterPath,
                genres = movie.genres.joinToString(" / ") { it.name ?: "" },
                runtimeText = runtimeText,
                releaseDateText = releaseDateText,
                youtubeTrailers = mapTrailers(movie.trailers, movie.videos),
                isWatched = movie.watched,
                isWatchlist = movie.watchlist,
                isTvShow = false,
                imdbId = movie.imdbId,
                reviews = reviewsUiModel,
                watchProviders = providersUiModel,
                castAndCrew = cast,
                similarMedia = similar,
                recommendations = recommendations,
                ratings = ratingsUiModel
            )
        } else if (tv != null) {
            val runtime = tv.episodeRunTime.firstOrNull() ?: 0
            val runtimeText = if (runtime > 0) "${runtime}m" else ""
            val releaseDate = tv.firstAirDate?.toReadableDate() ?: ""
            val releaseDateText = if (runtimeText.isNotEmpty() && releaseDate.isNotEmpty()) " • $releaseDate" else releaseDate

            return MediaDetailsUiState(
                mediaId = tv.id ?: 0,
                title = tv.name ?: "",
                overview = (tv.overview ?: "").parseHtml(),
                tagline = tv.tagline ?: "",
                backdropPath = tv.backdropPath,
                posterPath = tv.posterPath,
                genres = tv.genres.joinToString(" / ") { it.name ?: "" },
                runtimeText = runtimeText,
                releaseDateText = releaseDateText,
                youtubeTrailers = mapTrailers(tv.trailers, tv.videos),
                isWatched = movie?.watched ?: false,
                isWatchlist = movie?.watchlist ?: false,
                isTvShow = true,
                imdbId = null,
                reviews = reviewsUiModel,
                watchProviders = providersUiModel,
                castAndCrew = cast,
                similarMedia = similar,
                recommendations = recommendations,
                ratings = ratingsUiModel
            )
        }
        return null
    }

    private fun mapRatings(
        movie: MovieDetails?,
        tv: TvDetails?,
        omdbRatings: RatingResponse?
    ): RatingsUiModel? {
        val ratingsList = mutableListOf<RatingSourceUiModel>()

        // TMDB Rating
        val tmdbVoteAverage = movie?.voteAverage ?: tv?.voteAverage
        val id = movie?.id ?: tv?.id
        val isTv = tv != null
        val typePath = if (isTv) "tv" else "movie"

        if (tmdbVoteAverage != null && tmdbVoteAverage > 0) {
            ratingsList.add(
                RatingSourceUiModel(
                    source = RatingSource.TMDB,
                    value = String.format(Locale.getDefault(), "%.1f", tmdbVoteAverage),
                    url = "https://www.themoviedb.org/$typePath/$id"
                )
            )
        }

        // External Ratings
        omdbRatings?.ratings?.forEach { rating ->
            val source = when (rating.source) {
                "Internet Movie Database" -> RatingSource.IMDB
                "Rotten Tomatoes" -> RatingSource.ROTTEN_TOMATOES
                "Metacritic" -> RatingSource.METACRITIC
                else -> RatingSource.OTHER
            }
            
            val value = rating.value
            if (!value.isNullOrEmpty() && value != "N/A") {
                val url = when (source) {
                    RatingSource.IMDB -> omdbRatings.imdbID?.let { "https://www.imdb.com/title/$it" }
                    RatingSource.ROTTEN_TOMATOES -> omdbRatings.tomatoURL
                    RatingSource.METACRITIC -> {
                        val title = movie?.title ?: tv?.name
                        val smallTitle = title?.replace(' ', '-')?.lowercase()
                            ?.replace("[^\\d-a-z]".toRegex(), "")
                        "https://www.metacritic.com/movie/$smallTitle"
                    }
                    else -> null
                }
                ratingsList.add(
                    RatingSourceUiModel(
                        source = source,
                        value = value,
                        url = url
                    )
                )
            }
        }

        return if (ratingsList.isNotEmpty()) RatingsUiModel(ratingsList) else null
    }

    private fun mapTrailers(trailers: tech.salroid.filmy.data.local.model.Trailers?, videos: Videos?): List<Youtube>? {
        val youtubeList = mutableListOf<Youtube>()

        // Prefer videos (modern TMDB API)
        videos?.results?.forEach { video ->
            if (video.site == "YouTube") {
                youtubeList.add(
                    Youtube(
                        name = video.name,
                        size = video.size?.toString(),
                        source = video.key,
                        type = video.type
                    )
                )
            }
        }

        // Add from old trailers if not already present or as fallback
        trailers?.youtube?.forEach { youtube ->
            if (youtubeList.none { it.source == youtube.source }) {
                youtubeList.add(youtube)
            }
        }

        return youtubeList.ifEmpty { null }
    }

    private fun mapReviews(reviews: ReviewResponse?): ReviewResponseUiModel? {
        if (reviews?.results == null) return null
        
        val mappedResults = reviews.results.map { review ->
            val avatarPath = review.authorDetails?.avatarPath
            var finalAvatarUrl = if (avatarPath != null) "https://image.tmdb.org/t/p/w500$avatarPath" else null
            
            avatarPath?.let {
                if (it.contains("www.gravatar.com")) {
                    finalAvatarUrl = it.subSequence(1, it.length - 1).toString()
                }
            }

            ReviewUiModel(
                id = review.id ?: "",
                author = review.author ?: "",
                content = (review.content ?: "").parseHtml(),
                createdAt = review.createdAt?.toReadableDate() ?: "",
                authorAvatarUrl = finalAvatarUrl
            )
        }
        
        return ReviewResponseUiModel(results = mappedResults)
    }

    private fun mapWatchProviders(watchProviders: WatchProviderResponse?): WatchProvidersUiModel? {
        val results = watchProviders?.results ?: return null

        // Priority to 'US' or 'IN'. In a real app,
        // this should match the user's region or device locale.
        val countryData = results.US ?: results.IN ?: return null

        val stream = countryData.flatrate
        val buy = countryData.buy
        val rent = countryData.rent

        val allProviders = mutableListOf<ProviderUiModel>()

        stream.forEach {
            allProviders.add(
                ProviderUiModel(
                    it.providerId ?: 0,
                    it.providerName ?: "",
                    it.logoPath ?: ""
                )
            )
        }
        buy.forEach {
            allProviders.add(
                ProviderUiModel(
                    it.providerId ?: 0,
                    it.providerName ?: "",
                    it.logoPath ?: ""
                )
            )
        }
        rent.forEach {
            allProviders.add(
                ProviderUiModel(
                    it.providerId ?: 0,
                    it.providerName ?: "",
                    it.logoPath ?: ""
                )
            )
        }

        val distinctProviders = allProviders
            .distinctBy { it.id }
            .filter { it.logoPath.isNotEmpty() }

        if (distinctProviders.isEmpty() && countryData.link.isNullOrEmpty()) {
            return null
        }

        return WatchProvidersUiModel(
            link = countryData.link,
            providers = distinctProviders
        )
    }
}
