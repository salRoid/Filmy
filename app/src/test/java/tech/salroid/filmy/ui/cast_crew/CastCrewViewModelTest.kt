package tech.salroid.filmy.ui.cast_crew

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.CastCrewDetailsResponse
import tech.salroid.filmy.data.local.model.CastCrewMoviesResponse
import tech.salroid.filmy.data.local.model.CombinedCreditsResponse
import tech.salroid.filmy.data.local.model.ExternalIdsResponse
import tech.salroid.filmy.ui.home.MoviesRepository

// Every fetch here goes through .flowOn(Dispatchers.IO), a real thread hop -
// subscribe with Turbine before triggering, matching the pattern established
// for Franchise/Gallery/Season (advanceUntilIdle() can't wait for it).
class CastCrewViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var viewModel: CastCrewViewModel

    @Before
    fun setUp() {
        moviesRepository = mockk()
        viewModel = CastCrewViewModel(moviesRepository)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `getCastAndCrew populates uiStateCastAndCrew`() = runTest {
        val response = CastAndCrewResponse(id = 1)
        every { moviesRepository.getCastAndCrew("1") } returns flowOf(response)

        viewModel.uiStateCastAndCrew.test {
            assertNull(awaitItem())
            viewModel.getCastAndCrew("1")
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCastAndCrewTv populates the same uiStateCastAndCrew`() = runTest {
        val response = CastAndCrewResponse(id = 2)
        every { moviesRepository.getCastAndCrewTv("2") } returns flowOf(response)

        viewModel.uiStateCastAndCrew.test {
            assertNull(awaitItem())
            viewModel.getCastAndCrewTv("2")
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCastCrewDetails populates details and resets the error flag`() = runTest {
        val response = CastCrewDetailsResponse(name = "An Actor")
        every { moviesRepository.getCastCrewDetails("5") } returns flowOf(response)

        viewModel.uiStateCastCrewDetails.test {
            assertNull(awaitItem())
            viewModel.getCastCrewDetails("5")
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(false, viewModel.uiStateError.value)
    }

    @Test
    fun `getCastCrewDetails sets the error flag on failure`() = runTest {
        every { moviesRepository.getCastCrewDetails("5") } returns flow { throw RuntimeException("boom") }

        viewModel.uiStateError.test {
            assertEquals(false, awaitItem())
            viewModel.getCastCrewDetails("5")
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(viewModel.uiStateCastCrewDetails.value)
    }

    @Test
    fun `getCastCrewMovies populates uiStateCastCrewMovies`() = runTest {
        val response = CastCrewMoviesResponse(id = 3)
        every { moviesRepository.getCastCrewMovies("3") } returns flowOf(response)

        viewModel.uiStateCastCrewMovies.test {
            assertNull(awaitItem())
            viewModel.getCastCrewMovies("3")
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCastCrewTvShows populates the same uiStateCastCrewMovies`() = runTest {
        val response = CastCrewMoviesResponse(id = 4)
        every { moviesRepository.getCastCrewTvShows("4") } returns flowOf(response)

        viewModel.uiStateCastCrewMovies.test {
            assertNull(awaitItem())
            viewModel.getCastCrewTvShows("4")
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getCombinedCredits populates uiStateCombinedCredits`() = runTest {
        val response = CombinedCreditsResponse(id = 6)
        every { moviesRepository.getCombinedCredits("6") } returns flowOf(response)

        viewModel.uiStateCombinedCredits.test {
            assertNull(awaitItem())
            viewModel.getCombinedCredits("6")
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getPersonExternalIds populates uiStateExternalIds`() = runTest {
        val response = ExternalIdsResponse(imdbId = "nm123")
        every { moviesRepository.getPersonExternalIds("7") } returns flowOf(response)

        viewModel.uiStateExternalIds.test {
            assertNull(awaitItem())
            viewModel.getPersonExternalIds("7")
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
