package tech.salroid.filmy.ui.details

import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.AuthorDetails
import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.Collection
import tech.salroid.filmy.data.local.model.ContentRating
import tech.salroid.filmy.data.local.model.ContentRatingsResponse
import tech.salroid.filmy.data.local.model.CountryReleaseDates
import tech.salroid.filmy.data.local.model.ExternalIdsResponse
import tech.salroid.filmy.data.local.model.Genre
import tech.salroid.filmy.data.local.model.ProductionCompanies
import tech.salroid.filmy.data.local.model.RatingResponse
import tech.salroid.filmy.data.local.model.Ratings
import tech.salroid.filmy.data.local.model.ReleaseDateEntry
import tech.salroid.filmy.data.local.model.ReleaseDatesResponse
import tech.salroid.filmy.data.local.model.Review
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.data.local.model.Trailers
import tech.salroid.filmy.data.local.model.Videos
import tech.salroid.filmy.data.local.model.VideoResult
import tech.salroid.filmy.data.local.model.Youtube
import tech.salroid.filmy.data.local.model.tv.Networks
import tech.salroid.filmy.data.local.model.tv.Seasons
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.data.local.model.watch_providers.Buy
import tech.salroid.filmy.data.local.model.watch_providers.FlatRate
import tech.salroid.filmy.data.local.model.watch_providers.Rent
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderCountry
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.ui.common.model.RatingSource
import tech.salroid.filmy.utility.PreferenceHelper

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MediaDetailsMapperTest {

    private lateinit var mapper: MediaDetailsMapper

    @Before
    fun setUp() {
        mapper = MediaDetailsMapper(ApplicationProvider.getApplicationContext())
    }

    // --- movie path ---

    @Test
    fun `map builds a movie UI state with formatted runtime and release date`() {
        val movie = MovieDetails(
            id = 1,
            title = "A Movie",
            overview = "<b>Overview</b>",
            tagline = "A tagline",
            genres = arrayListOf(Genre(name = "Action"), Genre(name = "Adventure")),
            runtime = 125,
            releaseDate = "2024-01-15",
            watched = true,
            watchlist = false,
            imdbId = "tt1"
        )

        val result = mapper.map(movie, null, null, null, null, null, null, null)

        requireNotNull(result)
        assertEquals(1, result.mediaId)
        assertEquals("A Movie", result.title)
        assertEquals("Overview", result.overview)
        assertEquals("Action / Adventure", result.genres)
        assertEquals("2h 5m", result.runtimeText)
        assertEquals(" • 15 Jan 2024", result.releaseDateText)
        assertTrue(result.isWatched)
        assertTrue(!result.isWatchlist)
        assertEquals(false, result.isTvShow)
        assertEquals("tt1", result.imdbId)
    }

    @Test
    fun `map falls back to external ids for imdbId when the movie has none`() {
        val movie = MovieDetails(id = 1, imdbId = null)
        val externalIds = ExternalIdsResponse(imdbId = "tt99")

        val result = mapper.map(movie, null, null, null, null, null, null, null, externalIds = externalIds)

        assertEquals("tt99", result?.imdbId)
    }

    @Test
    fun `map omits runtime text when runtime is zero and uses the bare date`() {
        val movie = MovieDetails(id = 1, runtime = null, releaseDate = "2024-01-15")

        val result = mapper.map(movie, null, null, null, null, null, null, null)

        assertEquals("", result?.runtimeText)
        assertEquals("15 Jan 2024", result?.releaseDateText)
    }

    @Test
    fun `map only surfaces budget and revenue when positive`() {
        val zeroed = MovieDetails(id = 1, budget = 0, revenue = 0)
        val positive = MovieDetails(id = 2, budget = 1000, revenue = 2000)

        assertNull(mapper.map(zeroed, null, null, null, null, null, null, null)?.budget)
        assertNull(mapper.map(zeroed, null, null, null, null, null, null, null)?.revenue)
        assertEquals(1000L, mapper.map(positive, null, null, null, null, null, null, null)?.budget)
        assertEquals(2000L, mapper.map(positive, null, null, null, null, null, null, null)?.revenue)
    }

    @Test
    fun `map exposes the collection id and name when the movie belongs to one`() {
        val movie = MovieDetails(id = 1, belongsToCollection = Collection(id = 10, name = "A Franchise"))

        val result = mapper.map(movie, null, null, null, null, null, null, null)

        assertEquals(10, result?.collectionId)
        assertEquals("A Franchise", result?.collectionName)
    }

    @Test
    fun `map returns null when neither movie nor tv details are provided`() {
        assertNull(mapper.map(null, null, null, null, null, null, null, null))
    }

    // --- tv path ---

    @Test
    fun `map builds a tv UI state using the local MovieDetails row for watched-watchlist`() {
        val tv = TvDetails(
            id = 5,
            name = "A Show",
            overview = "Overview",
            episodeRunTime = arrayListOf(45),
            firstAirDate = "2024-01-15",
            genres = arrayListOf(Genre(name = "Drama"))
        )
        val local = MovieDetails(id = 5, type = 1, watched = true, watchlist = true, userRating = 8f)

        val result = mapper.map(local, tv, null, null, null, null, null, null)

        requireNotNull(result)
        assertEquals(5, result.mediaId)
        assertEquals("A Show", result.title)
        assertEquals("45m", result.runtimeText)
        assertTrue(result.isTvShow)
        assertTrue(result.isWatched)
        assertTrue(result.isWatchlist)
        assertEquals(8f, result.userRating)
    }

    @Test
    fun `map defaults tv watched-watchlist to false when there is no local row`() {
        val tv = TvDetails(id = 5, name = "A Show")

        val result = mapper.map(null, tv, null, null, null, null, null, null)

        assertEquals(false, result?.isWatched)
        assertEquals(false, result?.isWatchlist)
    }

    @Test
    fun `map only takes imdbId from externalIds for tv, never from the local row`() {
        val tv = TvDetails(id = 5, name = "A Show")
        val local = MovieDetails(id = 5, type = 1, imdbId = "tt-should-be-ignored")
        val externalIds = ExternalIdsResponse(imdbId = "tt-real")

        val result = mapper.map(local, tv, null, null, null, null, null, null, externalIds = externalIds)

        assertEquals("tt-real", result?.imdbId)
    }

    @Test
    fun `map builds season entries with sensible fallbacks`() {
        val tv = TvDetails(
            id = 5,
            name = "A Show",
            seasons = arrayListOf(
                Seasons(id = 1, seasonNumber = 1, name = null, episodeCount = 10),
                Seasons(id = null, seasonNumber = 2, name = "Season Two")
            )
        )

        val result = mapper.map(null, tv, null, null, null, null, null, null)

        // The season with a null id is dropped; the one with a null name falls back to "Season N".
        assertEquals(1, result?.seasons?.size)
        assertEquals("Season 1", result?.seasons?.first()?.name)
        assertEquals(10, result?.seasons?.first()?.episodeCount)
    }

    @Test
    fun `map maps tv networks as studios`() {
        val tv = TvDetails(id = 5, name = "A Show", networks = arrayListOf(Networks(id = 1, name = "HBO")))

        val result = mapper.map(null, tv, null, null, null, null, null, null)

        assertEquals(listOf("HBO"), result?.studios?.map { it.name })
    }

    // --- certification ---

    @Test
    fun `certification prefers the user's selected region over US`() {
        PreferenceHelper.setSelectedCountry(ApplicationProvider.getApplicationContext(), "IN")
        val releaseDates = ReleaseDatesResponse(
            results = listOf(
                CountryReleaseDates(iso31661 = "US", releaseDates = listOf(ReleaseDateEntry(certification = "PG-13"))),
                CountryReleaseDates(iso31661 = "IN", releaseDates = listOf(ReleaseDateEntry(certification = "U/A")))
            )
        )

        val result = mapper.map(MovieDetails(id = 1), null, null, null, null, null, null, null, releaseDates = releaseDates)

        assertEquals("U/A", result?.certification)
    }

    @Test
    fun `certification falls back to US when the preferred region has no data`() {
        PreferenceHelper.setSelectedCountry(ApplicationProvider.getApplicationContext(), "FR")
        val releaseDates = ReleaseDatesResponse(
            results = listOf(CountryReleaseDates(iso31661 = "US", releaseDates = listOf(ReleaseDateEntry(certification = "PG-13"))))
        )

        val result = mapper.map(MovieDetails(id = 1), null, null, null, null, null, null, null, releaseDates = releaseDates)

        assertEquals("PG-13", result?.certification)
    }

    @Test
    fun `certification falls back to content ratings when release dates have no certification`() {
        PreferenceHelper.setSelectedCountry(ApplicationProvider.getApplicationContext(), "US")
        val contentRatings = ContentRatingsResponse(results = listOf(ContentRating(iso31661 = "US", rating = "TV-14")))

        val result = mapper.map(
            null, TvDetails(id = 1, name = "A Show"), null, null, null, null, null, null,
            contentRatings = contentRatings
        )

        assertEquals("TV-14", result?.certification)
    }

    @Test
    fun `certification is null when nothing is available`() {
        val result = mapper.map(MovieDetails(id = 1), null, null, null, null, null, null, null)

        assertNull(result?.certification)
    }

    // --- ratings ---

    @Test
    fun `ratings include the user's own rating first`() {
        val movie = MovieDetails(id = 1, userRating = 7.5f)

        val result = mapper.map(movie, null, null, null, null, null, null, null)

        val userRating = result?.ratings?.ratings?.first()
        assertEquals(RatingSource.USER, userRating?.source)
        assertEquals("7.5/10", userRating?.value)
    }

    @Test
    fun `ratings include the TMDB score with the correct movie-vs-tv url`() {
        val movie = MovieDetails(id = 42, voteAverage = 8.234)

        val result = mapper.map(movie, null, null, null, null, null, null, null)

        val tmdb = result?.ratings?.ratings?.find { it.source == RatingSource.TMDB }
        assertEquals("8.2", tmdb?.value)
        assertEquals("https://www.themoviedb.org/movie/42", tmdb?.url)
    }

    @Test
    fun `ratings omit a zero TMDB score`() {
        val movie = MovieDetails(id = 1, voteAverage = 0.0)

        val result = mapper.map(movie, null, null, null, null, null, null, null)

        assertNull(result?.ratings?.ratings?.find { it.source == RatingSource.TMDB })
    }

    @Test
    fun `ratings map OMDB sources and skip N-A values`() {
        val omdb = RatingResponse(
            imdbID = "tt1",
            tomatoURL = "https://rt.example/movie",
            ratings = arrayListOf(
                Ratings(source = "Internet Movie Database", value = "8.5/10"),
                Ratings(source = "Rotten Tomatoes", value = "90%"),
                Ratings(source = "Metacritic", value = "N/A")
            )
        )

        val result = mapper.map(MovieDetails(id = 1), null, null, null, null, null, null, omdb)

        val sources = result?.ratings?.ratings.orEmpty()
        assertTrue(sources.any { it.source == RatingSource.IMDB && it.value == "8.5/10" && it.url == "https://www.imdb.com/title/tt1" })
        assertTrue(sources.any { it.source == RatingSource.ROTTEN_TOMATOES && it.url == "https://rt.example/movie" })
        assertTrue(sources.none { it.source == RatingSource.METACRITIC })
    }

    @Test
    fun `ratings is null when there is nothing to show`() {
        val result = mapper.map(MovieDetails(id = 1), null, null, null, null, null, null, null)

        assertNull(result?.ratings)
    }

    // --- trailers ---

    @Test
    fun `trailers prefer videos over the legacy trailers field and dedupe by source`() {
        val movie = MovieDetails(
            id = 1,
            videos = Videos(results = listOf(VideoResult(key = "abc", name = "Official Trailer", site = "YouTube", type = "Trailer"))),
            trailers = Trailers(youtube = arrayListOf(Youtube(source = "abc", name = "Duplicate"), Youtube(source = "xyz", name = "Extra")))
        )

        val result = mapper.map(movie, null, null, null, null, null, null, null)

        val sources = result?.youtubeTrailers?.map { it.source }
        assertEquals(listOf("abc", "xyz"), sources)
    }

    @Test
    fun `trailers are sorted with an actual Trailer leading over a Clip`() {
        val movie = MovieDetails(
            id = 1,
            videos = Videos(
                results = listOf(
                    VideoResult(key = "clip1", name = "A Clip", site = "YouTube", type = "Clip"),
                    VideoResult(key = "trailer1", name = "The Trailer", site = "YouTube", type = "Trailer")
                )
            )
        )

        val result = mapper.map(movie, null, null, null, null, null, null, null)

        assertEquals("trailer1", result?.youtubeTrailers?.first()?.source)
    }

    @Test
    fun `trailers ignore non-YouTube videos`() {
        val movie = MovieDetails(
            id = 1,
            videos = Videos(results = listOf(VideoResult(key = "v1", site = "Vimeo", type = "Trailer")))
        )

        val result = mapper.map(movie, null, null, null, null, null, null, null)

        assertNull(result?.youtubeTrailers)
    }

    // --- reviews ---

    @Test
    fun `reviews build a full TMDB-hosted avatar url`() {
        val reviews = ReviewResponse(
            results = arrayListOf(
                Review(
                    id = "r1",
                    author = "Someone",
                    content = "<p>Great movie</p>",
                    createdAt = "2024-01-15T00:00:00Z",
                    authorDetails = AuthorDetails(avatarPath = "/abc.jpg")
                )
            )
        )

        val result = mapper.map(MovieDetails(id = 1), null, reviews, null, null, null, null, null)

        val review = result?.reviews?.results?.first()
        assertEquals("Great movie", review?.content)
        assertEquals("https://image.tmdb.org/t/p/w500/abc.jpg", review?.authorAvatarUrl)
    }

    @Test
    fun `reviews strip the leading slash for a gravatar-hosted avatar`() {
        val reviews = ReviewResponse(
            results = arrayListOf(Review(id = "r1", authorDetails = AuthorDetails(avatarPath = "/https://www.gravatar.com/avatar/abc.jpg")))
        )

        val result = mapper.map(MovieDetails(id = 1), null, reviews, null, null, null, null, null)

        assertEquals("https://www.gravatar.com/avatar/abc.jpg", result?.reviews?.results?.first()?.authorAvatarUrl)
    }

    // --- watch providers ---

    @Test
    fun `watch providers prefer the selected region falling back to US`() {
        PreferenceHelper.setSelectedCountry(ApplicationProvider.getApplicationContext(), "IN")
        val watchProviders = WatchProviderResponse(
            results = mapOf(
                "US" to WatchProviderCountry(flatrate = arrayListOf(FlatRate(providerId = 1, providerName = "US Provider", logoPath = "/us.jpg"))),
                "IN" to WatchProviderCountry(flatrate = arrayListOf(FlatRate(providerId = 2, providerName = "IN Provider", logoPath = "/in.jpg")))
            )
        )

        val result = mapper.map(MovieDetails(id = 1), null, null, watchProviders, null, null, null, null)

        assertEquals(listOf("IN Provider"), result?.watchProviders?.providers?.map { it.name })
    }

    @Test
    fun `watch providers merge stream buy and rent and dedupe by provider id`() {
        PreferenceHelper.setSelectedCountry(ApplicationProvider.getApplicationContext(), "US")
        val watchProviders = WatchProviderResponse(
            results = mapOf(
                "US" to WatchProviderCountry(
                    flatrate = arrayListOf(FlatRate(providerId = 1, providerName = "Netflix", logoPath = "/n.jpg")),
                    buy = arrayListOf(Buy(providerId = 1, providerName = "Netflix", logoPath = "/n.jpg")),
                    rent = arrayListOf(Rent(providerId = 2, providerName = "Apple TV", logoPath = "/a.jpg"))
                )
            )
        )

        val result = mapper.map(MovieDetails(id = 1), null, null, watchProviders, null, null, null, null)

        assertEquals(2, result?.watchProviders?.providers?.size)
    }

    @Test
    fun `watch providers filter out entries with no logo`() {
        PreferenceHelper.setSelectedCountry(ApplicationProvider.getApplicationContext(), "US")
        val watchProviders = WatchProviderResponse(
            results = mapOf("US" to WatchProviderCountry(flatrate = arrayListOf(FlatRate(providerId = 1, providerName = "No Logo", logoPath = ""))))
        )

        val result = mapper.map(MovieDetails(id = 1), null, null, watchProviders, null, null, null, null)

        assertNull(result?.watchProviders)
    }

    @Test
    fun `watch providers surface just the link when there are no providers`() {
        PreferenceHelper.setSelectedCountry(ApplicationProvider.getApplicationContext(), "US")
        val watchProviders = WatchProviderResponse(results = mapOf("US" to WatchProviderCountry(link = "https://tmdb.example/watch")))

        val result = mapper.map(MovieDetails(id = 1), null, null, watchProviders, null, null, null, null)

        assertEquals("https://tmdb.example/watch", result?.watchProviders?.link)
    }

    // --- toMovieDetails ---

    @Test
    fun `toMovieDetails creates a new row from tv details when there is no existing one`() {
        val tv = TvDetails(id = 5, name = "A Show", overview = "Overview", firstAirDate = "2024-01-15")

        val result = tv.toMovieDetails(existing = null, watched = true, watchlist = false)

        assertEquals(5, result.id)
        assertEquals(1, result.type)
        assertEquals("A Show", result.title)
        assertTrue(result.watched)
        assertTrue(!result.watchlist)
    }

    @Test
    fun `toMovieDetails preserves other fields of an existing row`() {
        val existing = MovieDetails(id = 5, type = 1, userRating = 9f)
        val tv = TvDetails(id = 5, name = "A Show")

        val result = tv.toMovieDetails(existing = existing, watched = true, watchlist = true)

        assertEquals(9f, result.userRating)
        assertEquals("A Show", result.title)
    }
}
