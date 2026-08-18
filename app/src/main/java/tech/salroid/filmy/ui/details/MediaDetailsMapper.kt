package tech.salroid.filmy.ui.details

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.ContentRatingsResponse
import tech.salroid.filmy.data.local.model.ExternalIdsResponse
import tech.salroid.filmy.data.local.model.ImagesResponse
import tech.salroid.filmy.data.local.model.Keyword
import tech.salroid.filmy.data.local.model.ProductionCompanies
import tech.salroid.filmy.data.local.model.RatingResponse
import tech.salroid.filmy.data.local.model.ReleaseDatesResponse
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.data.local.model.tv.Networks
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.data.local.model.Videos
import tech.salroid.filmy.data.local.model.Youtube
import tech.salroid.filmy.ui.common.model.*
import tech.salroid.filmy.utility.PreferenceHelper
import tech.salroid.filmy.utility.parseHtml
import tech.salroid.filmy.utility.toReadableDate
import java.util.Locale
import javax.inject.Inject

class MediaDetailsMapper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    fun map(
        movie: MovieDetails?,
        tv: TvDetails?,
        reviews: ReviewResponse?,
        watchProviders: WatchProviderResponse?,
        cast: CastAndCrewResponse?,
        similar: SimilarMoviesResponse?,
        recommendations: SimilarMoviesResponse?,
        ratings: RatingResponse?,
        releaseDates: ReleaseDatesResponse? = null,
        contentRatings: ContentRatingsResponse? = null,
        keywords: List<Keyword>? = null,
        images: ImagesResponse? = null,
        externalIds: ExternalIdsResponse? = null
    ): MediaDetailsUiState? {
        val providersUiModel = mapWatchProviders(watchProviders)
        val reviewsUiModel = mapReviews(reviews)
        val ratingsUiModel = mapRatings(movie, tv, ratings)
        val certification = mapCertification(releaseDates, contentRatings)
        val awards = ratings?.awards?.takeIf { it.isNotBlank() && it != "N/A" }
        val backdropImages = images?.backdrops?.mapNotNull { it.filePath }?.take(8)?.ifEmpty { null }

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
                imdbId = movie.imdbId ?: externalIds?.imdbId,
                reviews = reviewsUiModel,
                watchProviders = providersUiModel,
                castAndCrew = cast,
                similarMedia = similar,
                recommendations = recommendations,
                ratings = ratingsUiModel,
                certification = certification,
                collectionId = movie.belongsToCollection?.id,
                collectionName = movie.belongsToCollection?.name,
                userRating = movie.userRating,
                awards = awards,
                budget = movie.budget?.takeIf { it > 0 },
                revenue = movie.revenue?.takeIf { it > 0 },
                studios = mapStudios(movie.productionCompanies),
                keywords = keywords,
                backdropImages = backdropImages,
                homepage = movie.homepage?.takeIf { it.isNotBlank() },
                facebookId = externalIds?.facebookId,
                instagramId = externalIds?.instagramId,
                twitterId = externalIds?.twitterId
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
                imdbId = externalIds?.imdbId,
                reviews = reviewsUiModel,
                watchProviders = providersUiModel,
                castAndCrew = cast,
                similarMedia = similar,
                recommendations = recommendations,
                ratings = ratingsUiModel,
                certification = certification,
                seasons = mapSeasons(tv),
                userRating = movie?.userRating,
                awards = awards,
                studios = mapNetworksAsStudios(tv.networks),
                keywords = keywords,
                backdropImages = backdropImages,
                homepage = tv.homepage?.takeIf { it.isNotBlank() },
                facebookId = externalIds?.facebookId,
                instagramId = externalIds?.instagramId,
                twitterId = externalIds?.twitterId
            )
        }
        return null
    }

    private fun mapSeasons(tv: TvDetails): List<SeasonUiModel>? {
        val seasons = tv.seasons.mapNotNull { season ->
            val id = season.id ?: return@mapNotNull null
            val seasonNumber = season.seasonNumber ?: return@mapNotNull null
            SeasonUiModel(
                id = id,
                seasonNumber = seasonNumber,
                name = season.name ?: "Season $seasonNumber",
                episodeCount = season.episodeCount ?: 0,
                posterPath = season.posterPath,
                airDate = season.airDate
            )
        }
        return seasons.ifEmpty { null }
    }

    private fun mapStudios(productionCompanies: List<ProductionCompanies>): List<StudioUiModel>? {
        val studios = productionCompanies.mapNotNull { company ->
            val id = company.id ?: return@mapNotNull null
            StudioUiModel(id = id, name = company.name ?: return@mapNotNull null, logoPath = company.logoPath)
        }
        return studios.ifEmpty { null }
    }

    private fun mapNetworksAsStudios(networks: List<Networks>): List<StudioUiModel>? {
        val studios = networks.mapNotNull { network ->
            val id = network.id ?: return@mapNotNull null
            StudioUiModel(id = id, name = network.name ?: return@mapNotNull null, logoPath = network.logoPath)
        }
        return studios.ifEmpty { null }
    }

    private fun mapCertification(
        releaseDates: ReleaseDatesResponse?,
        contentRatings: ContentRatingsResponse?
    ): String? {
        // Priority to the user's selected region, falling back to 'US'.
        val preferred = PreferenceHelper.getSelectedCountry(context)

        releaseDates?.results?.let { results ->
            val country = results.find { it.iso31661 == preferred } ?: results.find { it.iso31661 == "US" }
            val certification = country?.releaseDates
                ?.firstOrNull { !it.certification.isNullOrBlank() }
                ?.certification
            if (!certification.isNullOrBlank()) return certification
        }

        contentRatings?.results?.let { results ->
            val rating = (results.find { it.iso31661 == preferred } ?: results.find { it.iso31661 == "US" })?.rating
            if (!rating.isNullOrBlank()) return rating
        }

        return null
    }

    private fun mapRatings(
        movie: MovieDetails?,
        tv: TvDetails?,
        omdbRatings: RatingResponse?
    ): RatingsUiModel? {
        val ratingsList = mutableListOf<RatingSourceUiModel>()

        // Your Rating
        movie?.userRating?.let { rating ->
            ratingsList.add(
                RatingSourceUiModel(
                    source = RatingSource.USER,
                    value = "$rating/10"
                )
            )
        }

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

        // Lead with an actual Trailer (falling back through Teaser/Clip/etc.)
        // so the featured thumbnail and top of "all videos" aren't a Blooper/Clip.
        return youtubeList.sortedBy { videoTypePriority(it.type) }.ifEmpty { null }
    }

    private fun videoTypePriority(type: String?): Int = when (type) {
        "Trailer" -> 0
        "Teaser" -> 1
        "Clip" -> 2
        "Featurette" -> 3
        "Behind the Scenes" -> 4
        "Bloopers" -> 5
        else -> 6
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

        // Priority to the user's selected region, falling back to 'US'.
        val preferred = PreferenceHelper.getSelectedCountry(context)
        val countryData = results[preferred] ?: results["US"] ?: return null

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

fun TvDetails.toMovieDetails(
    existing: MovieDetails?,
    watched: Boolean,
    watchlist: Boolean
): MovieDetails = (existing ?: MovieDetails(id = this.id ?: 0, type = 1)).copy(
    title = this.name,
    overview = this.overview,
    tagline = this.tagline,
    backdropPath = this.backdropPath,
    posterPath = this.posterPath,
    voteAverage = this.voteAverage,
    voteCount = this.voteCount?.toLong(),
    originalLanguage = this.originalLanguage,
    releaseDate = this.firstAirDate
).apply {
    this.watched = watched
    this.watchlist = watchlist
}
