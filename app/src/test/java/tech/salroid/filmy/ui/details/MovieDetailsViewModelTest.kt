package tech.salroid.filmy.ui.details

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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
import tech.salroid.filmy.data.local.model.account.ListItemsResponse
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
        coEvery { accountSyncRepository.pushItemState(any(), any()) } returns true

        viewModel.toggleWatched(movie)

        coVerify(timeout = 1000) { accountSyncRepository.pushItemState(movie.copy(watched = true), any()) }
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
        coEvery { accountSyncRepository.pushItemState(updated, any()) } returns false

        viewModel.updateMovieDetailsInDb(updated, "watched", remove = false, previous = previous)

        coVerify(timeout = 1000) { accountSyncRepository.pushItemState(updated, any()) }
        verify(timeout = 1000) { moviesRepository.addMovieDetailsToLocal(previous) }
        assertEquals(Triple(4, "watched", true), viewModel.uiStateUpdateCollection.value)
    }

    @Test
    fun `updateMovieDetailsInDb deletes the row when the push fails and there was no previous row`() = runTest {
        val updated = MovieDetails(id = 5, type = 0, watched = true)
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        every { moviesRepository.deleteMovieDetailsFromLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(updated, any()) } returns false

        viewModel.updateMovieDetailsInDb(updated, "watched", remove = false, previous = null)

        verify(timeout = 1000) { moviesRepository.deleteMovieDetailsFromLocal(updated) }
    }

    @Test
    fun `updateMovieDetailsInDb keeps the optimistic write when the push succeeds`() = runTest {
        val updated = MovieDetails(id = 6, type = 0, watched = true)
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(updated, any()) } returns true

        viewModel.updateMovieDetailsInDb(updated, "watched", remove = false, previous = null)

        coVerify(timeout = 1000) { accountSyncRepository.pushItemState(updated, any()) }
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

    private fun waitFor(condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + 2000
        while (!condition() && System.currentTimeMillis() < deadline) Thread.sleep(10)
    }

    @Test
    fun `loadUserLists fetches lists and asks TMDB which ones hold the title`() = runTest {
        every { accountRepository.canManageLists() } returns true
        val watchLater = TmdbList(id = 10, name = "Watch later")
        val favourites = TmdbList(id = 11, name = "Favourites")
        coEvery { accountRepository.getLists() } returns
            flowOf(TmdbListsResponse(results = listOf(watchLater, favourites)))
        coEvery { accountRepository.isInList(10, 55, true) } returns flowOf(true)
        coEvery { accountRepository.isInList(11, 55, true) } returns flowOf(false)

        viewModel.loadUserLists(55, isTv = true)

        // Each list's membership arrives on its own, so wait for both.
        waitFor { viewModel.listMembership.value.size == 2 }
        assertEquals(mapOf(10 to true, 11 to false), viewModel.listMembership.value)
        assertEquals(listOf(watchLater, favourites), viewModel.userLists.value)
        assertEquals(false, viewModel.userListsLoading.value)
    }

    @Test
    fun `loadUserLists reports loading until the lists have arrived`() = runTest {
        every { accountRepository.canManageLists() } returns true
        val gate = kotlinx.coroutines.CompletableDeferred<Unit>()
        coEvery { accountRepository.getLists() } returns flow {
            gate.await()
            emit(TmdbListsResponse(results = emptyList()))
        }

        viewModel.loadUserLists(55, isTv = false)
        waitFor { viewModel.userListsLoading.value }
        assertEquals(true, viewModel.userListsLoading.value)

        gate.complete(Unit)
        waitFor { !viewModel.userListsLoading.value }
        assertEquals(false, viewModel.userListsLoading.value)
    }

    @Test
    fun `a membership check that fails leaves the list usable as not-a-member`() = runTest {
        every { accountRepository.canManageLists() } returns true
        coEvery { accountRepository.getLists() } returns
            flowOf(TmdbListsResponse(results = listOf(TmdbList(id = 10, name = "Watch later"))))
        coEvery { accountRepository.isInList(10, 55, false) } returns flow { throw RuntimeException("offline") }

        viewModel.loadUserLists(55, isTv = false)

        waitFor { viewModel.listMembership.value.containsKey(10) }
        assertEquals(mapOf(10 to false), viewModel.listMembership.value)
    }

    @Test
    fun `isInAnyList turns true once any list holds the title`() = runTest {
        every { accountRepository.canManageLists() } returns true
        coEvery { accountRepository.getLists() } returns flowOf(
            TmdbListsResponse(results = listOf(TmdbList(id = 10, name = "A"), TmdbList(id = 11, name = "B")))
        )
        coEvery { accountRepository.isInList(10, 55, false) } returns flowOf(false)
        coEvery { accountRepository.isInList(11, 55, false) } returns flowOf(true)

        viewModel.isInAnyList.test {
            assertEquals(false, awaitItem())
            viewModel.loadUserLists(55, isTv = false)
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `reloading the lists refreshes in place without a loader or losing membership`() = runTest {
        every { accountRepository.canManageLists() } returns true
        coEvery { accountRepository.getLists() } returns
            flowOf(TmdbListsResponse(results = listOf(TmdbList(id = 10, name = "A"))))
        coEvery { accountRepository.isInList(10, 55, false) } returns flowOf(true)
        viewModel.loadUserLists(55, isTv = false)
        waitFor { viewModel.listMembership.value[10] == true }

        val loaderStates = mutableListOf<Boolean>()
        val job = backgroundScope.launch(kotlinx.coroutines.Dispatchers.Unconfined) {
            viewModel.userListsLoading.collect { loaderStates += it }
        }
        viewModel.loadUserLists(55, isTv = false)
        coVerify(timeout = 1000, exactly = 2) { accountRepository.isInList(10, 55, false) }
        job.cancel()

        assertEquals(listOf(false), loaderStates)
        assertEquals(true, viewModel.listMembership.value[10])
    }

    // --- toggleListMembership ---

    @Test
    fun `toggleListMembership adds a show optimistically and keeps it on success`() = runTest {
        every { accountRepository.canManageLists() } returns true
        coEvery { accountRepository.addToList(10, 55, true) } returns flowOf(ListItemsResponse(success = true))

        viewModel.toggleListMembership(10, 55, isTv = true, currentlyIn = false)

        assertEquals(true, viewModel.listMembership.value[10])
        coVerify(timeout = 1000) { accountRepository.addToList(10, 55, true) }
        assertEquals(true, viewModel.listMembership.value[10])
    }

    @Test
    fun `toggleListMembership rolls back without list access`() = runTest {
        every { accountRepository.canManageLists() } returns false

        viewModel.toggleListMembership(10, 55, isTv = false, currentlyIn = false)

        assertEquals(true, viewModel.listMembership.value[10])
        verify(timeout = 1000, exactly = 1) { accountRepository.canManageLists() }
        waitFor { viewModel.listMembership.value[10] == false }
        assertEquals(false, viewModel.listMembership.value[10])
    }

    @Test
    fun `toggling keeps the list's shown item count in step`() = runTest {
        every { accountRepository.canManageLists() } returns true
        coEvery { accountRepository.getLists() } returns
            flowOf(TmdbListsResponse(results = listOf(TmdbList(id = 10, name = "Watch later", itemCount = 3))))
        coEvery { accountRepository.isInList(10, 55, false) } returns flowOf(false)
        coEvery { accountRepository.addToList(10, 55, false) } returns flowOf(ListItemsResponse(success = true))
        coEvery { accountRepository.removeFromList(10, 55, false) } returns flowOf(ListItemsResponse(success = true))
        viewModel.loadUserLists(55, isTv = false)
        waitFor { viewModel.listMembership.value.containsKey(10) }

        viewModel.toggleListMembership(10, 55, isTv = false, currentlyIn = false)
        assertEquals(4, viewModel.userLists.value.single().itemCount)
        // The row is locked until the add has landed.
        waitFor { viewModel.pendingListIds.value.isEmpty() }

        viewModel.toggleListMembership(10, 55, isTv = false, currentlyIn = true)
        assertEquals(3, viewModel.userLists.value.single().itemCount)
    }

    @Test
    fun `a list with a change in flight ignores further taps until it lands`() = runTest {
        every { accountRepository.canManageLists() } returns true
        val gate = kotlinx.coroutines.CompletableDeferred<Unit>()
        coEvery { accountRepository.addToList(10, 55, false) } returns flow {
            gate.await()
            emit(ListItemsResponse(success = true))
        }

        viewModel.toggleListMembership(10, 55, isTv = false, currentlyIn = false)
        assertEquals(setOf(10), viewModel.pendingListIds.value)

        // A second tap while the add is still on its way must not start a remove.
        viewModel.toggleListMembership(10, 55, isTv = false, currentlyIn = true)
        assertEquals(true, viewModel.listMembership.value[10])

        gate.complete(Unit)
        waitFor { viewModel.pendingListIds.value.isEmpty() }
        assertEquals(emptySet<Int>(), viewModel.pendingListIds.value)
        assertEquals(true, viewModel.listMembership.value[10])
        coVerify(exactly = 0) { accountRepository.removeFromList(any(), any(), any()) }
    }

    // --- createList ---

    @Test
    fun `createList makes the list and puts this title in it`() = runTest {
        every { accountRepository.canManageLists() } returns true
        coEvery { accountRepository.createList("New list") } returns flowOf(CreateListResponse(listId = 99))
        coEvery { accountRepository.addToList(99, 55, true) } returns flowOf(ListItemsResponse(success = true))

        viewModel.createList("New list", 55, isTv = true)

        waitFor { viewModel.listMembership.value[99] == true }
        val created = viewModel.userLists.value.single()
        assertEquals(99, created.id)
        assertEquals("New list", created.name)
        assertEquals(1, created.itemCount)
        assertEquals(true, viewModel.listMembership.value[99])
    }

    @Test
    fun `createList keeps the new list even if adding the title to it fails`() = runTest {
        every { accountRepository.canManageLists() } returns true
        coEvery { accountRepository.createList("New list") } returns flowOf(CreateListResponse(listId = 99))
        coEvery { accountRepository.addToList(99, 55, false) } returns flow { throw RuntimeException("offline") }

        viewModel.createList("New list", 55, isTv = false)

        coVerify(timeout = 1000) { accountRepository.addToList(99, 55, false) }
        waitFor { viewModel.userLists.value.isNotEmpty() }
        assertEquals(0, viewModel.userLists.value.single().itemCount)
        assertEquals(false, viewModel.listMembership.value[99])
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
