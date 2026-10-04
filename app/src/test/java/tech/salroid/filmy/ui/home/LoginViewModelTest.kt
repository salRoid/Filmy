package tech.salroid.filmy.ui.home

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.login.DeleteSession
import tech.salroid.filmy.data.local.model.login.RequestTokenResponse
import tech.salroid.filmy.data.local.model.login.SessionDataResponse

class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var accountRepository: AccountRepository
    private var createdViewModel: LoginViewModel? = null

    // repairStaleLocalProfile() runs from init{}, always calling isLoggedIn(),
    // so every test must stub it before constructing the ViewModel.
    private fun viewModel(loggedInAtStart: Boolean = false): LoginViewModel {
        every { accountRepository.isLoggedIn() } returns loggedInAtStart
        if (!loggedInAtStart) {
            every { accountRepository.clearProfile() } returns 0
        }
        every { accountRepository.getProfileFlow() } returns flowOf(null)
        stubListAccess()
        return LoginViewModel(accountRepository).also { createdViewModel = it }
    }

    // canManageLists is built at construction from both of these.
    private fun stubListAccess() {
        every { accountRepository.canManageLists() } returns false
        every { accountRepository.canManageListsFlow() } returns flowOf(false)
    }

    // repairStaleLocalProfile() launches with Dispatchers.IO - a real
    // dispatcher hop outside the test's virtual time - so a just-finished
    // test's coroutine can still be winding down when the next test's
    // MainDispatcherRule resets Main.
    @After
    fun tearDown() {
        createdViewModel?.viewModelScope?.cancel()
    }

    @Test
    fun `init clears a stale local profile when logged out`() {
        accountRepository = mockk()

        viewModel(loggedInAtStart = false)

        verify(timeout = 1000, exactly = 1) { accountRepository.clearProfile() }
    }

    @Test
    fun `init does not touch the local profile when already logged in`() {
        accountRepository = mockk()
        every { accountRepository.isLoggedIn() } returns true
        every { accountRepository.getProfileFlow() } returns flowOf(null)
        stubListAccess()

        LoginViewModel(accountRepository).also { createdViewModel = it }

        verify(timeout = 1000) { accountRepository.isLoggedIn() }
        verify(exactly = 0) { accountRepository.clearProfile() }
    }

    @Test
    fun `getRequestToken stores the token and emits it`() = runTest {
        accountRepository = mockk()
        val viewModel = viewModel()
        val response = RequestTokenResponse(requestToken = "req-token", success = true)
        coEvery { accountRepository.getRequestToken(any()) } returns flowOf(response)

        viewModel.uiStateToken.test {
            assertNull(awaitItem())
            viewModel.getRequestToken()
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertEquals("req-token", viewModel.requestToken)
    }

    @Test
    fun `getRequestToken clears isAuthenticating on failure`() = runTest {
        accountRepository = mockk()
        val viewModel = viewModel()
        coEvery { accountRepository.getRequestToken(any()) } returns flow { throw RuntimeException("boom") }

        viewModel.isAuthenticating.test {
            assertEquals(false, awaitItem())
            viewModel.getRequestToken()
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `full login chain stores the session, the profile and then list access`() = runTest {
        accountRepository = mockk()
        val viewModel = viewModel()
        viewModel.requestToken = "req-token"

        coEvery { accountRepository.getAccessToken(any()) } returns
            flowOf(RequestTokenResponse(accessToken = "access-token", accountId = "account-object-id", success = true))
        every { accountRepository.getSessionIdFromPref() } returns null
        every { accountRepository.storeUserAccessToken("access-token", "account-object-id") } returns Unit
        coEvery { accountRepository.getSession(any()) } returns
            flowOf(SessionDataResponse(sessionId = "session-id", success = true))
        val profile = Profile(id = 1, name = "Someone")
        coEvery { accountRepository.getProfile("session-id") } returns flowOf(profile)
        every { accountRepository.storeSessionId("session-id") } returns Unit
        every { accountRepository.saveProfileToLocal(profile) } returns Unit

        viewModel.getAccessToken()

        // getAccessToken/getSession/getProfile never actually set isAuthenticating
        // true on the success path (only getRequestToken does, and only error
        // paths flip it), so there's no state-flow transition to await here -
        // verify(timeout=...) on the terminal side effects is the sync point.
        verify(timeout = 1000) { accountRepository.storeUserAccessToken("access-token", "account-object-id") }
        assertEquals("access-token", viewModel.accessToken)
        assertEquals("session-id", viewModel.sessionId)
        verifyOrder {
            accountRepository.storeSessionId("session-id")
            accountRepository.saveProfileToLocal(profile)
            accountRepository.storeUserAccessToken("access-token", "account-object-id")
        }
        verify(exactly = 0) { accountRepository.deleteSession(any()) }
    }

    @Test
    fun `logging in again over an existing session revokes the old one`() = runTest {
        accountRepository = mockk()
        val viewModel = viewModel(loggedInAtStart = true)
        viewModel.requestToken = "req-token"

        coEvery { accountRepository.getAccessToken(any()) } returns
            flowOf(RequestTokenResponse(accessToken = "access-token", accountId = "account-object-id", success = true))
        every { accountRepository.getSessionIdFromPref() } returns "old-session"
        coEvery { accountRepository.getSession(any()) } returns
            flowOf(SessionDataResponse(sessionId = "new-session", success = true))
        coEvery { accountRepository.deleteSession("old-session") } returns flowOf(DeleteSession(success = true))
        val profile = Profile(id = 1, name = "Someone")
        coEvery { accountRepository.getProfile("new-session") } returns flowOf(profile)
        every { accountRepository.storeSessionId("new-session") } returns Unit
        every { accountRepository.saveProfileToLocal(profile) } returns Unit
        every { accountRepository.storeUserAccessToken("access-token", "account-object-id") } returns Unit

        viewModel.getAccessToken()

        verify(timeout = 1000) { accountRepository.deleteSession("old-session") }
        verify(timeout = 1000) { accountRepository.storeUserAccessToken("access-token", "account-object-id") }
    }

    @Test
    fun `getAccessToken clears the request token when exchange fails`() = runTest {
        accountRepository = mockk()
        val viewModel = viewModel()
        // getAccessToken itself never sets isAuthenticating true (only
        // getRequestToken does, synchronously, before its coroutine even
        // launches) - go through it first to get a real true state to
        // transition away from, rather than racing a same-value no-op emission.
        coEvery { accountRepository.getRequestToken(any()) } returns
            flowOf(RequestTokenResponse(requestToken = "req-token"))

        viewModel.isAuthenticating.test {
            assertEquals(false, awaitItem())
            viewModel.getRequestToken()
            assertEquals(true, awaitItem())

            coEvery { accountRepository.getAccessToken(any()) } returns flow { throw RuntimeException("closed tab") }
            viewModel.getAccessToken()
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        assertNull(viewModel.requestToken)
    }

    @Test
    fun `logout always clears local state even when the session id is null`() = runTest {
        accountRepository = mockk()
        val viewModel = viewModel()
        every { accountRepository.getSessionIdFromPref() } returns null
        every { accountRepository.getUserAccessToken() } returns null
        val cleanupGate = CountDownLatch(1)
        every { accountRepository.clearProfile() } answers {
            cleanupGate.await(2, TimeUnit.SECONDS)
            0
        }
        every { accountRepository.storeSessionId(null) } returns Unit
        every { accountRepository.storeUserAccessToken(null, null) } returns Unit

        viewModel.isLoggingOut.test {
            assertEquals(false, awaitItem())
            viewModel.logout()
            assertEquals(true, awaitItem())
            cleanupGate.countDown()
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(timeout = 1000) { accountRepository.storeSessionId(null) }
        verify(timeout = 1000) { accountRepository.storeUserAccessToken(null, null) }
        verify(exactly = 0) { accountRepository.revokeUserAccessToken(any()) }
    }

    @Test
    fun `logout best-effort revokes the server session and the user's token when they exist`() = runTest {
        accountRepository = mockk()
        val viewModel = viewModel()
        every { accountRepository.getSessionIdFromPref() } returns "session-id"
        every { accountRepository.getUserAccessToken() } returns "access-token"
        coEvery { accountRepository.deleteSession("session-id") } returns flowOf(DeleteSession(success = true))
        // A failed revoke must not stop the local logout.
        coEvery { accountRepository.revokeUserAccessToken("access-token") } returns flow { throw RuntimeException("offline") }
        val cleanupGate = CountDownLatch(1)
        every { accountRepository.clearProfile() } answers {
            cleanupGate.await(2, TimeUnit.SECONDS)
            0
        }
        every { accountRepository.storeSessionId(null) } returns Unit
        every { accountRepository.storeUserAccessToken(null, null) } returns Unit

        viewModel.isLoggingOut.test {
            assertEquals(false, awaitItem())
            viewModel.logout()
            assertEquals(true, awaitItem())
            cleanupGate.countDown()
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(timeout = 1000) { accountRepository.deleteSession("session-id") }
        verify { accountRepository.revokeUserAccessToken("access-token") }
        verify { accountRepository.storeSessionId(null) }
        verify { accountRepository.storeUserAccessToken(null, null) }
    }
}
