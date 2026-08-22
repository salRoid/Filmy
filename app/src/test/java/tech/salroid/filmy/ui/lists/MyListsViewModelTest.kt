package tech.salroid.filmy.ui.lists

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.account.CreateListResponse
import tech.salroid.filmy.data.local.model.account.TmdbList
import tech.salroid.filmy.data.local.model.account.TmdbListsResponse
import tech.salroid.filmy.data.local.model.account.TmdbStatusResponse
import tech.salroid.filmy.ui.home.AccountRepository

class MyListsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var accountRepository: AccountRepository
    private var createdViewModel: MyListsViewModel? = null

    // MyListsViewModel calls loadLists() from init{}, so the repository stubs
    // it needs must be set up before construction - each test builds its own
    // instance instead of sharing one from @Before.
    private fun viewModel(): MyListsViewModel =
        MyListsViewModel(accountRepository).also { createdViewModel = it }

    // loadLists()/createList()/deleteList() launch with Dispatchers.IO - a
    // real dispatcher hop outside the test's virtual time - so a
    // just-finished test's coroutine can still be winding down when the
    // next test's MainDispatcherRule resets Main.
    @After
    fun tearDown() {
        createdViewModel?.viewModelScope?.cancel()
    }

    @Test
    fun `init loads the user's lists when logged in`() = runTest {
        accountRepository = mockk()
        every { accountRepository.getSessionIdFromPref() } returns "session"
        every { accountRepository.getProfileFromLocal() } returns Profile(id = 1)
        val list = TmdbList(id = 1, name = "Watch later")
        coEvery { accountRepository.getLists(1, "session") } returns
            flowOf(TmdbListsResponse(results = listOf(list)))

        // init{}'s loadLists() dispatches onto a real thread (Dispatchers.IO)
        // immediately on construction - it can complete before this test even
        // reaches a Turbine subscription, so asserting an "initial empty" first
        // item would race. Synchronize on the network call instead.
        val viewModel = viewModel()
        coVerify(timeout = 1000) { accountRepository.getLists(1, "session") }

        assertEquals(listOf(list), viewModel.lists.value)
    }

    @Test
    fun `init does nothing when there is no session`() {
        accountRepository = mockk()
        every { accountRepository.getSessionIdFromPref() } returns null

        val viewModel = viewModel()

        assertEquals(emptyList<TmdbList>(), viewModel.lists.value)
    }

    @Test
    fun `createList appends the new list locally on success`() = runTest {
        accountRepository = mockk()
        every { accountRepository.getSessionIdFromPref() } returns null // skip init's loadLists

        val viewModel = viewModel()
        every { accountRepository.getSessionIdFromPref() } returns "session"
        coEvery { accountRepository.createList("session", "New list") } returns
            flowOf(CreateListResponse(listId = 42))

        viewModel.lists.test {
            assertEquals(emptyList<TmdbList>(), awaitItem())
            viewModel.createList("New list")
            val updated = awaitItem()
            assertEquals(1, updated.size)
            assertEquals(42, updated.first().id)
            assertEquals("New list", updated.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // deleteList's optimistic removal (synchronous) and its async success/rollback
    // (Dispatchers.IO, a real thread) can conflate on this StateFlow before a slow
    // Turbine collector observes the intermediate "removed" state, so rather than
    // racing multiple awaitItem() calls: assert the synchronous removal directly,
    // synchronize the async branch via a timeout-based verify on a call unique to
    // it, then read the settled value once.

    @Test
    fun `deleteList removes optimistically and keeps it removed on success`() = runTest {
        accountRepository = mockk()
        every { accountRepository.getSessionIdFromPref() } returns "session"
        every { accountRepository.getProfileFromLocal() } returns Profile(id = 1)
        val list = TmdbList(id = 9, name = "Doomed")
        coEvery { accountRepository.getLists(1, "session") } returns
            flowOf(TmdbListsResponse(results = listOf(list)))
        coEvery { accountRepository.deleteList(9, "session") } returns flowOf(TmdbStatusResponse())

        val viewModel = viewModel()
        verify(timeout = 1000) { accountRepository.getLists(1, "session") }

        viewModel.deleteList(9)

        assertEquals(emptyList<TmdbList>(), viewModel.lists.value)
        coVerify(timeout = 1000) { accountRepository.deleteList(9, "session") }
        assertEquals(emptyList<TmdbList>(), viewModel.lists.value)
    }

    @Test
    fun `deleteList rolls back when there is no session at push time`() = runTest {
        accountRepository = mockk()
        every { accountRepository.getSessionIdFromPref() } returns "session"
        every { accountRepository.getProfileFromLocal() } returns Profile(id = 1)
        val list = TmdbList(id = 9, name = "Doomed")
        coEvery { accountRepository.getLists(1, "session") } returns
            flowOf(TmdbListsResponse(results = listOf(list)))

        val viewModel = viewModel()
        verify(timeout = 1000) { accountRepository.getLists(1, "session") }

        every { accountRepository.getSessionIdFromPref() } returns null
        viewModel.deleteList(9)

        assertEquals(emptyList<TmdbList>(), viewModel.lists.value)
        // 2nd call overall (1st was init's loadLists) - waiting for it confirms
        // the rollback branch has run to completion.
        verify(timeout = 1000, exactly = 2) { accountRepository.getSessionIdFromPref() }
        assertEquals(listOf(list), viewModel.lists.value)
    }
}
