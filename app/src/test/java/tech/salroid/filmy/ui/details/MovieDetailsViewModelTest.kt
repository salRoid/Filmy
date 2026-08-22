package tech.salroid.filmy.ui.details

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.ReviewResponse
import tech.salroid.filmy.data.local.model.account.CreateListResponse
import tech.salroid.filmy.data.local.model.account.TmdbList
import tech.salroid.filmy.data.local.model.account.TmdbListDetailsResponse
import tech.salroid.filmy.data.local.model.account.TmdbListsResponse
import tech.salroid.filmy.data.local.model.account.TmdbStatusResponse
import tech.salroid.filmy.data.local.model.tv.TvDetails
import tech.salroid.filmy.ui.home.AccountRepository
import tech.salroid.filmy.ui.home.AccountSyncRepository
import tech.salroid.filmy.ui.home.MoviesRepository
import kotlin.time.Duration.Companion.seconds

class MovieDetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var mediaDetailsMapper: MediaDetailsMapper
    private lateinit var accountSyncRepository: AccountSyncRepository
    private lateinit var accountRepository: AccountRepository
    private lateinit var viewModel: MovieDetailsViewModel

    @Before
    fun setUp() {
        moviesRepository = mockk()
        mediaDetailsMapper = mockk(relaxed = true)
        accountSyncRepository = mockk()
        accountRepository = mockk()
        viewModel = MovieDetailsViewModel(moviesRepository, mediaDetailsMapper, accountSyncRepository, accountRepository)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
    }

    // --- getMovieDetails ---

    @Test
    fun `getMovieDetails merges local watched-watchlist state onto the network response`() = runTest {
        val local = MovieDetails(id = 1, type = 0, watched = true, watchlist = false, userRating = 7f)
        every { moviesRepository.getMovieDetailsFlow(1, 0) } returns flowOf(null)
        every { moviesRepository.getMovieDetailsFromLocal(1, 0) } returns local
        val network = MovieDetails(id = 1, type = 0, title = "A Movie")
        every { moviesRepository.getMovieDetailsFromNetwork("1") } returns flowOf(network)
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit

        viewModel.getMovieDetails("1", 0, addToLocal = false)

        val expected = network.copy(watched = true, watchlist = false, userRating = 7f, type = 0)
        verify(timeout = 1000) { moviesRepository.addMovieDetailsToLocal(expected) }
    }

    @Test
    fun `getMovieDetails emits directly instead of saving when not locally tracked and addToLocal is false`() = runTest {
        every { moviesRepository.getMovieDetailsFlow(1, 0) } returns flowOf(null)
        every { moviesRepository.getMovieDetailsFromLocal(1, 0) } returns null
        val network = MovieDetails(id = 1, type = 0, title = "A Movie")
        every { moviesRepository.getMovieDetailsFromNetwork("1") } returns flowOf(network)

        // getMovieDetails dispatches onto a real Dispatchers.IO thread; Turbine's
        // default 3s timeout was observed to occasionally trip under heavy system
        // load (e.g. running the full suite back-to-back many times) even though
        // the mocked work itself is instant - a more generous margin avoids that.
        viewModel.uiStateMovieDetails.test(timeout = 10.seconds) {
            assertEquals(null, awaitItem())
            viewModel.getMovieDetails("1", 0, addToLocal = false)
            assertEquals(network.copy(type = 0), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(exactly = 0) { moviesRepository.addMovieDetailsToLocal(any()) }
    }

    @Test
    fun `getMovieDetails sets the error flag when the network fails and there is no local copy`() = runTest {
        every { moviesRepository.getMovieDetailsFlow(1, 0) } returns flowOf(null)
        every { moviesRepository.getMovieDetailsFromLocal(1, 0) } returns null
        every { moviesRepository.getMovieDetailsFromNetwork("1") } returns flow { throw RuntimeException("offline") }

        viewModel.uiStateError.test {
            assertEquals(false, awaitItem())
            viewModel.getMovieDetails("1", 0, addToLocal = false)
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getMovieDetails does not set the error flag when a local copy exists`() = runTest {
        every { moviesRepository.getMovieDetailsFlow(1, 0) } returns flowOf(null)
        every { moviesRepository.getMovieDetailsFromLocal(1, 0) } returns MovieDetails(id = 1, type = 0)
        every { moviesRepository.getMovieDetailsFromNetwork("1") } returns flow { throw RuntimeException("offline") }

        viewModel.getMovieDetails("1", 0, addToLocal = false)

        verify(timeout = 1000) { moviesRepository.getMovieDetailsFromLocal(1, 0) }
        assertFalse(viewModel.uiStateError.value)
    }

    // --- getTvDetails ---

    @Test
    fun `getTvDetails updates the local row's TV-specific fields when already tracked`() = runTest {
        val local = MovieDetails(id = 2, type = 1, watched = true)
        every { moviesRepository.getMovieDetailsFlow(2, 1) } returns flowOf(null)
        every { moviesRepository.getMovieDetailsFromLocal(2, 1) } returns local
        val showDetails = TvDetails(id = 2, name = "A Show", overview = "Overview")
        every { moviesRepository.getTvShowDetailsFromNetwork("2") } returns flowOf(showDetails)
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit

        viewModel.getTvDetails("2", 1, addToLocal = false)

        verify(timeout = 1000) {
            moviesRepository.addMovieDetailsToLocal(
                match { it.id == 2 && it.title == "A Show" && it.overview == "Overview" && it.watched }
            )
        }
    }

    // --- toggleWatched / toggleWatchlist / updateMovieDetailsInDb ---

    @Test
    fun `toggleWatched flips watched and writes it optimistically`() = runTest {
        val movie = MovieDetails(id = 3, type = 0, watched = false)
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(any()) } returns true

        viewModel.toggleWatched(movie)

        coVerify(timeout = 1000) { accountSyncRepository.pushItemState(movie.copy(watched = true)) }
        verify { moviesRepository.addMovieDetailsToLocal(movie.copy(watched = true)) }
    }

    @Test
    fun `updateMovieDetailsInDb rolls back to the previous row when the push fails`() = runTest {
        // Two emissions land close together on this StateFlow (optimistic, then
        // rollback) - conflation could drop the first before a slow collector
        // observes it, so synchronize on the mock call instead of racing two
        // sequential awaitItem() calls, then check the settled final state.
        val previous = MovieDetails(id = 4, type = 0, watched = false)
        val updated = previous.copy(watched = true)
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(updated) } returns false

        viewModel.updateMovieDetailsInDb(updated, "watched", remove = false, previous = previous)

        coVerify(timeout = 1000) { accountSyncRepository.pushItemState(updated) }
        verify(timeout = 1000) { moviesRepository.addMovieDetailsToLocal(previous) }
        assertEquals(Triple(4, "watched", true), viewModel.uiStateUpdateCollection.value)
    }

    @Test
    fun `updateMovieDetailsInDb deletes the row when the push fails and there was no previous row`() = runTest {
        val updated = MovieDetails(id = 5, type = 0, watched = true)
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        every { moviesRepository.deleteMovieDetailsFromLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(updated) } returns false

        viewModel.updateMovieDetailsInDb(updated, "watched", remove = false, previous = null)

        verify(timeout = 1000) { moviesRepository.deleteMovieDetailsFromLocal(updated) }
    }

    @Test
    fun `updateMovieDetailsInDb keeps the optimistic write when the push succeeds`() = runTest {
        val updated = MovieDetails(id = 6, type = 0, watched = true)
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(updated) } returns true

        viewModel.updateMovieDetailsInDb(updated, "watched", remove = false, previous = null)

        coVerify(timeout = 1000) { accountSyncRepository.pushItemState(updated) }
        verify(exactly = 0) { moviesRepository.deleteMovieDetailsFromLocal(any()) }
        verify(exactly = 1) { moviesRepository.addMovieDetailsToLocal(updated) }
    }

    // --- rateMovie ---

    @Test
    fun `rateMovie writes the rating optimistically and keeps it on success`() = runTest {
        val movie = MovieDetails(id = 7, type = 0, userRating = null)
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushRating(any()) } returns true

        viewModel.rateMovie(movie, 9f)

        coVerify(timeout = 1000) { accountSyncRepository.pushRating(movie.copy(userRating = 9f)) }
        verify(exactly = 0) { moviesRepository.addMovieDetailsToLocal(movie) }
    }

    @Test
    fun `rateMovie rolls back to the previous rating when the push fails`() = runTest {
        val movie = MovieDetails(id = 7, type = 0, userRating = 5f)
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushRating(movie.copy(userRating = 9f)) } returns false

        viewModel.rateMovie(movie, 9f)

        verify(timeout = 1000) { moviesRepository.addMovieDetailsToLocal(movie) }
    }

    // --- loadUserLists ---

    @Test
    fun `loadUserLists fetches lists and computes membership`() = runTest {
        every { accountRepository.getSessionIdFromPref() } returns "session"
        every { accountRepository.getProfileFromLocal() } returns Profile(id = 1)
        val list = TmdbList(id = 10, name = "Watch later")
        coEvery { accountRepository.getLists(1, "session") } returns flowOf(TmdbListsResponse(results = listOf(list)))
        coEvery { accountRepository.getListDetails(10, "session") } returns
            flowOf(TmdbListDetailsResponse(id = 10, items = listOf(tech.salroid.filmy.data.local.db.entity.Movie(id = 55))))

        // listMembership only emits once in this function (unlike
        // uiStateUpdateCollection elsewhere), so awaiting it via Turbine is safe -
        // and since it's written after userLists in the same coroutine, seeing it
        // update also guarantees userLists has already settled.
        viewModel.listMembership.test {
            assertEquals(emptyMap<Int, Boolean>(), awaitItem())
            viewModel.loadUserLists(55)
            assertEquals(mapOf(10 to true), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals(listOf(list), viewModel.userLists.value)
    }

    // --- toggleListMembership ---

    @Test
    fun `toggleListMembership flips membership optimistically and keeps it on success`() = runTest {
        every { accountRepository.getSessionIdFromPref() } returns "session"
        coEvery { accountRepository.addToList(10, "session", 55) } returns flowOf(TmdbStatusResponse())

        viewModel.toggleListMembership(10, 55, currentlyIn = false)

        assertEquals(true, viewModel.listMembership.value[10])
        coVerify(timeout = 1000) { accountRepository.addToList(10, "session", 55) }
        assertEquals(true, viewModel.listMembership.value[10])
    }

    @Test
    fun `toggleListMembership rolls back when there is no session`() = runTest {
        every { accountRepository.getSessionIdFromPref() } returns null

        viewModel.toggleListMembership(10, 55, currentlyIn = false)

        assertEquals(true, viewModel.listMembership.value[10])
        verify(timeout = 1000, exactly = 1) { accountRepository.getSessionIdFromPref() }
        assertEquals(false, viewModel.listMembership.value[10])
    }

    // --- createList ---

    @Test
    fun `createList appends the new list on success`() = runTest {
        every { accountRepository.getSessionIdFromPref() } returns "session"
        coEvery { accountRepository.createList("session", "New list") } returns flowOf(CreateListResponse(listId = 99))

        viewModel.userLists.test {
            assertEquals(emptyList<TmdbList>(), awaitItem())
            viewModel.createList("New list")
            val updated = awaitItem()
            assertEquals(1, updated.size)
            assertEquals(99, updated.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // --- misc ---

    @Test
    fun `isLoggedIn delegates to AccountRepository`() {
        every { accountRepository.isLoggedIn() } returns true

        assertTrue(viewModel.isLoggedIn())
    }

    @Test
    fun `getReviews populates uiStateReviewResponse`() = runTest {
        val response = ReviewResponse(id = 1)
        every { moviesRepository.getReviews("1") } returns flowOf(response)

        viewModel.uiStateReviewResponse.test {
            assertEquals(null, awaitItem())
            viewModel.getReviews("1")
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
