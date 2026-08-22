package tech.salroid.filmy.ui.season

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.data.local.model.tv.SeasonDetailsResponse
import tech.salroid.filmy.ui.home.MoviesRepository

class SeasonViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var viewModel: SeasonViewModel

    @Before
    fun setUp() {
        moviesRepository = mockk()
        viewModel = SeasonViewModel(moviesRepository)
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
}
