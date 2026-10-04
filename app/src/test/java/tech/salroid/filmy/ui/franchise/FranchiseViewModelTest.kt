package tech.salroid.filmy.ui.franchise

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.cancelScopeAndJoin
import tech.salroid.filmy.data.local.model.collection.CollectionDetailsResponse
import tech.salroid.filmy.ui.home.MoviesRepository

class FranchiseViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var viewModel: FranchiseViewModel

    @Before
    fun setUp() {
        moviesRepository = mockk()
        viewModel = FranchiseViewModel(moviesRepository)
    }

    @After
    fun tearDown() {
        viewModel.cancelScopeAndJoin()
    }

    // loadCollection's repository call goes through .flowOn(Dispatchers.IO), a
    // real thread hop that advanceUntilIdle() (which only drains the virtual
    // Main-test scheduler) can't wait on - subscribe with Turbine first and let
    // it genuinely suspend for the update instead of racing a `.value` read.

    @Test
    fun `loadCollection populates the collection`() = runTest {
        val response = CollectionDetailsResponse(id = 10, name = "A Franchise")
        every { moviesRepository.getCollectionDetails(10) } returns flowOf(response)

        viewModel.collection.test {
            assertNull(awaitItem())
            viewModel.loadCollection(10)
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `loadCollection clears isLoading even when the fetch fails`() = runTest {
        every { moviesRepository.getCollectionDetails(10) } returns flow {
            throw RuntimeException("network error")
        }

        viewModel.isLoading.test {
            assertEquals(false, awaitItem())
            viewModel.loadCollection(10)
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(viewModel.collection.value)
    }
}
