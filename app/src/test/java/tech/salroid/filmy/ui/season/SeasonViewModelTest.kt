package tech.salroid.filmy.ui.season

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.data.local.model.ExternalIdsResponse
import tech.salroid.filmy.data.local.model.OmdbEpisodeRating
import tech.salroid.filmy.data.local.model.OmdbSeasonResponse
import tech.salroid.filmy.data.local.model.tv.SeasonDetailsResponse
import tech.salroid.filmy.data.local.model.watch_providers.WatchProviderResponse
import tech.salroid.filmy.ui.common.model.ProviderUiModel
import tech.salroid.filmy.ui.common.model.WatchProvidersUiModel
import tech.salroid.filmy.ui.details.MediaDetailsMapper
import tech.salroid.filmy.ui.home.MoviesRepository

class SeasonViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var mediaDetailsMapper: MediaDetailsMapper
    private lateinit var viewModel: SeasonViewModel

    @Before
    fun setUp() {
        moviesRepository = mockk()
        mediaDetailsMapper = mockk()
        // loadSeason always fires the episode-ratings and watch-providers
        // fetches too (best-effort, alongside the main season fetch), so
        // every test needs these stubbed even when it isn't asserting on
        // them - otherwise the unstubbed calls throw inside those
        // coroutines.
        every { moviesRepository.getTvExternalIds(any()) } returns flowOf(ExternalIdsResponse())
        every { moviesRepository.getWatchProvidersTv(any()) } returns flowOf(WatchProviderResponse())
        every { mediaDetailsMapper.mapWatchProviders(any()) } returns null
        viewModel = SeasonViewModel(moviesRepository, mediaDetailsMapper)
    }

    // loadSeason fires three independent coroutines (season, episode
    // ratings, watch providers); each test here only awaits the one it's
    // asserting on via Turbine, leaving the other two to keep running on
    // the real Dispatchers.IO hop introduced by flowOn(Dispatchers.IO) -
    // cancelled here so they don't try to resume on Main after
    // MainDispatcherRule has already reset it.
    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `loadSeason populates the season`() = runTest {
        val response = SeasonDetailsResponse(id = 1, seasonNumber = 2, name = "Season 2")
        every { moviesRepository.getSeasonDetails("77", 2) } returns flowOf(response)

        viewModel.season.test {
            assertNull(awaitItem())
            viewModel.loadSeason("77", 2)
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `episodeRatings is populated from OMDB season ratings, keyed by episode number`() = runTest {
        every { moviesRepository.getSeasonDetails(any(), any()) } returns flowOf(SeasonDetailsResponse())
        every { moviesRepository.getTvExternalIds("77") } returns flowOf(ExternalIdsResponse(imdbId = "tt123"))
        every { moviesRepository.getSeasonRatings("tt123", 2) } returns flowOf(
            OmdbSeasonResponse(
                episodes = listOf(
                    OmdbEpisodeRating(episode = 1, imdbRating = "8.5"),
                    OmdbEpisodeRating(episode = 2, imdbRating = "N/A"),
                    OmdbEpisodeRating(episode = 3, imdbRating = null)
                )
            )
        )

        viewModel.episodeRatings.test {
            assertEquals(emptyMap<Int, String>(), awaitItem())
            viewModel.loadSeason("77", 2)
            assertEquals(mapOf(1 to "8.5/10"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `episodeRatings stays empty when the show has no imdb id`() = runTest {
        every { moviesRepository.getSeasonDetails(any(), any()) } returns flowOf(SeasonDetailsResponse())
        every { moviesRepository.getTvExternalIds("77") } returns flowOf(ExternalIdsResponse(imdbId = null))

        viewModel.episodeRatings.test {
            assertEquals(emptyMap<Int, String>(), awaitItem())
            viewModel.loadSeason("77", 2)
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `watchProviders is populated from the mapped TV watch-provider response`() = runTest {
        every { moviesRepository.getSeasonDetails(any(), any()) } returns flowOf(SeasonDetailsResponse())
        val response = WatchProviderResponse(id = 77)
        val mapped = WatchProvidersUiModel(
            link = "https://www.themoviedb.org",
            providers = listOf(ProviderUiModel(1, "Netflix", "/logo.jpg"))
        )
        every { moviesRepository.getWatchProvidersTv("77") } returns flowOf(response)
        every { mediaDetailsMapper.mapWatchProviders(response) } returns mapped

        viewModel.watchProviders.test {
            assertNull(awaitItem())
            viewModel.loadSeason("77", 2)
            assertEquals(mapped, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
