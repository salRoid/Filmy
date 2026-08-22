package tech.salroid.filmy.ui.home

import androidx.lifecycle.viewModelScope
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
        return LoginViewModel(accountRepository).also { createdViewModel = it }
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
    fun `full login chain stores the session and saves the profile`() = runTest {
        accountRepository = mockk()
        val viewModel = viewModel()
        viewModel.requestToken = "req-token"

        coEvery { accountRepository.getAccessToken(any()) } returns
            flowOf(RequestTokenResponse(accessToken = "access-token", success = true))
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
        verify(timeout = 1000) { accountRepository.saveProfileToLocal(profile) }
        assertEquals("access-token", viewModel.accessToken)
        assertEquals("session-id", viewModel.sessionId)
        verify { accountRepository.storeSessionId("session-id") }
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
        every { accountRepository.clearProfile() } returns 0
        every { accountRepository.storeSessionId(null) } returns Unit

        viewModel.isLoggingOut.test {
            assertEquals(false, awaitItem())
            viewModel.logout()
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(timeout = 1000) { accountRepository.storeSessionId(null) }
    }

    @Test
    fun `logout best-effort revokes the server session when one exists`() = runTest {
        accountRepository = mockk()
        val viewModel = viewModel()
        every { accountRepository.getSessionIdFromPref() } returns "session-id"
        coEvery { accountRepository.deleteSession("session-id") } returns flowOf(DeleteSession(success = true))
        every { accountRepository.clearProfile() } returns 0
        every { accountRepository.storeSessionId(null) } returns Unit

        viewModel.isLoggingOut.test {
            assertEquals(false, awaitItem())
            viewModel.logout()
            assertEquals(true, awaitItem())
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(timeout = 1000) { accountRepository.deleteSession("session-id") }
        verify { accountRepository.storeSessionId(null) }
    }
}
