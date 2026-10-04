package tech.salroid.filmy.ui.lists

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
import tech.salroid.filmy.data.local.model.account.ListItemResult
import tech.salroid.filmy.data.local.model.account.ListItemsResponse
import tech.salroid.filmy.data.local.model.account.TmdbListDetailsResponse
import tech.salroid.filmy.data.local.model.account.TmdbListItem
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
    fun `loadListDetails does nothing without list access`() = runTest {
        every { accountRepository.canManageLists() } returns false

        viewModel.loadListDetails(1)

        assertEquals(emptyList<TmdbListItem>(), viewModel.items.value)
    }

    @Test
    fun `loadListDetails gathers every page, movies and shows alike`() = runTest {
        every { accountRepository.canManageLists() } returns true
        val movie = TmdbListItem(id = 1, mediaType = "movie", title = "A Movie")
        val show = TmdbListItem(id = 2, mediaType = "tv", name = "A Show")
        coEvery { accountRepository.getListDetails(5, 1) } returns
            flowOf(TmdbListDetailsResponse(id = 5, items = listOf(movie), totalPages = 2))
        coEvery { accountRepository.getListDetails(5, 2) } returns
            flowOf(TmdbListDetailsResponse(id = 5, items = listOf(show), totalPages = 2))

        viewModel.items.test {
            assertEquals(emptyList<TmdbListItem>(), awaitItem())
            viewModel.loadListDetails(5)
            assertEquals(listOf(movie, show), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `removeItem removes optimistically and keeps it removed on success`() = runTest {
        val show = TmdbListItem(id = 7, mediaType = "tv", name = "To Remove")
        // Same id as the show, but a movie: it must survive the removal.
        val movie = TmdbListItem(id = 7, mediaType = "movie", title = "Keeps")
        every { accountRepository.canManageLists() } returns true
        coEvery { accountRepository.getListDetails(1, 1) } returns
            flowOf(TmdbListDetailsResponse(id = 1, items = listOf(show, movie)))
        coEvery { accountRepository.removeFromList(1, 7, true) } returns flowOf(ListItemsResponse(success = true))

        viewModel.items.test {
            assertEquals(emptyList<TmdbListItem>(), awaitItem())
            viewModel.loadListDetails(1)
            assertEquals(listOf(show, movie), awaitItem())

            viewModel.removeItem(1, show)
            assertEquals(listOf(movie), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(timeout = 1000) { accountRepository.removeFromList(1, 7, true) }
        assertEquals(listOf(movie), viewModel.items.value)
    }

    @Test
    fun `removeItem rolls back when TMDB rejects the removal`() = runTest {
        val movie = TmdbListItem(id = 7, mediaType = "movie", title = "To Remove")
        val other = TmdbListItem(id = 8, mediaType = "movie", title = "Keeps")
        every { accountRepository.canManageLists() } returns true
        coEvery { accountRepository.getListDetails(1, 1) } returns
            flowOf(TmdbListDetailsResponse(id = 1, items = listOf(movie, other)))
        coEvery { accountRepository.removeFromList(1, 7, false) } returns
            flowOf(ListItemsResponse(success = true, results = listOf(ListItemResult(mediaId = 7, success = false))))

        viewModel.items.test {
            assertEquals(emptyList<TmdbListItem>(), awaitItem())
            viewModel.loadListDetails(1)
            assertEquals(listOf(movie, other), awaitItem())

            viewModel.removeItem(1, movie)
            // The optimistic removal and its async rollback can conflate on this
            // StateFlow before a slow collector observes the intermediate
            // "removed" state - synchronize on the push itself instead of
            // racing multiple awaitItem() calls.
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(timeout = 1000) { accountRepository.removeFromList(1, 7, false) }
        // The rollback is the statement right after the push returns.
        val deadline = System.currentTimeMillis() + 1000
        while (viewModel.items.value != listOf(movie, other) && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }
        assertEquals(listOf(movie, other), viewModel.items.value)
    }
}
