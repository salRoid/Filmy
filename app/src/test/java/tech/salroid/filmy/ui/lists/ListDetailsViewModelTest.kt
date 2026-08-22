package tech.salroid.filmy.ui.lists

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.model.account.TmdbListDetailsResponse
import tech.salroid.filmy.data.local.model.account.TmdbStatusResponse
import tech.salroid.filmy.ui.home.AccountRepository

class ListDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var accountRepository: AccountRepository
    private lateinit var viewModel: ListDetailsViewModel

    @Before
    fun setUp() {
        accountRepository = mockk()
        viewModel = ListDetailsViewModel(accountRepository)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `loadListDetails does nothing when there is no session`() = runTest {
        every { accountRepository.getSessionIdFromPref() } returns null

        viewModel.loadListDetails(1)

        assertEquals(emptyList<Movie>(), viewModel.items.value)
    }

    @Test
    fun `loadListDetails populates items from the response`() = runTest {
        every { accountRepository.getSessionIdFromPref() } returns "session"
        val movie = Movie(id = 1, title = "A Movie")
        coEvery { accountRepository.getListDetails(5, "session") } returns
            flowOf(TmdbListDetailsResponse(id = 5, items = listOf(movie)))

        viewModel.items.test {
            assertEquals(emptyList<Movie>(), awaitItem())
            viewModel.loadListDetails(5)
            assertEquals(listOf(movie), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeItem removes optimistically and keeps it removed on success`() = runTest {
        val movie = Movie(id = 7, title = "To Remove")
        val other = Movie(id = 8, title = "Keeps")
        every { accountRepository.getSessionIdFromPref() } returns "session"
        coEvery { accountRepository.getListDetails(1, "session") } returns
            flowOf(TmdbListDetailsResponse(id = 1, items = listOf(movie, other)))
        coEvery { accountRepository.removeFromList(1, "session", 7) } returns flowOf(TmdbStatusResponse())

        viewModel.items.test {
            assertEquals(emptyList<Movie>(), awaitItem())
            viewModel.loadListDetails(1)
            assertEquals(listOf(movie, other), awaitItem())

            viewModel.removeItem(1, movie)
            assertEquals(listOf(other), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeItem rolls back when there is no session at push time`() = runTest {
        val movie = Movie(id = 7, title = "To Remove")
        val other = Movie(id = 8, title = "Keeps")
        every { accountRepository.getSessionIdFromPref() } returns "session"
        coEvery { accountRepository.getListDetails(1, "session") } returns
            flowOf(TmdbListDetailsResponse(id = 1, items = listOf(movie, other)))

        viewModel.items.test {
            assertEquals(emptyList<Movie>(), awaitItem())
            viewModel.loadListDetails(1)
            assertEquals(listOf(movie, other), awaitItem())

            every { accountRepository.getSessionIdFromPref() } returns null
            viewModel.removeItem(1, movie)
            // The optimistic removal and its async rollback can conflate on this
            // StateFlow before a slow collector observes the intermediate
            // "removed" state - synchronize on the rollback branch's own
            // getSessionIdFromPref() call (2nd overall: 1st was loadListDetails)
            // instead of racing multiple awaitItem() calls.
            cancelAndIgnoreRemainingEvents()
        }
        verify(timeout = 1000, exactly = 2) { accountRepository.getSessionIdFromPref() }
        assertEquals(listOf(movie, other), viewModel.items.value)
    }
}
