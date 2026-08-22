package tech.salroid.filmy.ui.collections

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.ui.home.AccountSyncRepository
import tech.salroid.filmy.ui.home.MoviesRepository

class CollectionsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var accountSyncRepository: AccountSyncRepository
    private lateinit var viewModel: CollectionsViewModel

    private val watchedItem = MovieDetails(id = 1, type = 0, watched = true)
    private val watchlistItem = MovieDetails(id = 2, type = 0, watchlist = true)

    @Before
    fun setUp() {
        moviesRepository = mockk()
        accountSyncRepository = mockk()

        // watched/watchlist are stateIn'd eagerly in the constructor, so these
        // must be stubbed before CollectionsViewModel is constructed.
        every { moviesRepository.getWatched() } returns flowOf(listOf(watchedItem))
        every { moviesRepository.getWatchlist() } returns flowOf(listOf(watchlistItem))

        viewModel = CollectionsViewModel(moviesRepository, accountSyncRepository)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `watched and watchlist expose what the repository provides`() = runTest {
        // watched/watchlist are WhileSubscribed StateFlows - they only start
        // collecting the upstream once something actually subscribes, so keep
        // a collector alive for the test the same way the real UI would.
        viewModel.watched.onEach { }.launchIn(backgroundScope)
        viewModel.watchlist.onEach { }.launchIn(backgroundScope)
        advanceUntilIdle()

        assertEquals(listOf(watchedItem), viewModel.watched.value)
        assertEquals(listOf(watchlistItem), viewModel.watchlist.value)
    }

    // trySync/removeWatched/removeWatchlist all launch on Dispatchers.IO directly
    // (a real thread pool, not the swapped Main test dispatcher), so
    // advanceUntilIdle() can't be relied on to wait for them. isSyncing gives
    // trySync a StateFlow to genuinely await via Turbine; removeWatched/
    // removeWatchlist have no such signal, so their first assertion uses
    // MockK's timeout-based verify to actually wait for the real thread hop -
    // once that's confirmed, further assertions on the same (by-then-finished)
    // coroutine are safe without a timeout.

    @Test
    fun `trySync delegates to AccountSyncRepository and clears isSyncing`() = runTest {
        coEvery { accountSyncRepository.syncIfNeeded() } returns Unit

        viewModel.isSyncing.test {
            assertEquals(false, awaitItem())
            viewModel.trySync()
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        coVerify(exactly = 1) { accountSyncRepository.syncIfNeeded() }
    }

    @Test
    fun `removeWatched writes the local change and keeps it when the push succeeds`() = runTest {
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(any()) } returns true

        viewModel.removeWatched(watchedItem)

        coVerify(timeout = 1000) { accountSyncRepository.pushItemState(watchedItem.copy(watched = false)) }
        verify(exactly = 1) { moviesRepository.addMovieDetailsToLocal(watchedItem.copy(watched = false)) }
        verify(exactly = 0) { moviesRepository.addMovieDetailsToLocal(watchedItem) }
    }

    @Test
    fun `removeWatched rolls back the local change when the push fails`() = runTest {
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(any()) } returns false

        viewModel.removeWatched(watchedItem)

        verify(timeout = 1000, exactly = 1) { moviesRepository.addMovieDetailsToLocal(watchedItem) }
        verify(exactly = 1) { moviesRepository.addMovieDetailsToLocal(watchedItem.copy(watched = false)) }
    }

    @Test
    fun `removeWatchlist rolls back the local change when the push fails`() = runTest {
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(any()) } returns false

        viewModel.removeWatchlist(watchlistItem)

        verify(timeout = 1000, exactly = 1) { moviesRepository.addMovieDetailsToLocal(watchlistItem) }
        verify(exactly = 1) { moviesRepository.addMovieDetailsToLocal(watchlistItem.copy(watchlist = false)) }
    }
}
