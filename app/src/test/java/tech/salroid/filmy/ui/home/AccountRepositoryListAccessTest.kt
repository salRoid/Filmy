package tech.salroid.filmy.ui.home

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import tech.salroid.filmy.FakeSharedPreferences
import tech.salroid.filmy.data.local.model.account.TmdbListsResponse
import tech.salroid.filmy.data.network.AccountApiHelper

/**
 * Lists run on TMDB v4 with the user's own access token, stored separately
 * from the v3 session - these cover that storage and the gating around it.
 */
class AccountRepositoryListAccessTest {

    private val prefs = FakeSharedPreferences()
    private val accountApiHelper: AccountApiHelper = mockk()
    private val repository = AccountRepository(prefs, mockk(), accountApiHelper)

    @Test
    fun `list access needs both the user token and the v4 account id`() {
        assertFalse(repository.canManageLists())

        repository.storeUserAccessToken("token", null)
        assertFalse(repository.canManageLists())

        repository.storeUserAccessToken("token", "account-object-id")
        assertTrue(repository.canManageLists())
        assertEquals("token", repository.getUserAccessToken())
    }

    @Test
    fun `a session on its own does not grant list access`() {
        repository.storeSessionId("session")

        assertTrue(repository.isLoggedIn())
        assertFalse(repository.canManageLists())
    }

    @Test
    fun `clearing the token on logout removes list access`() {
        repository.storeUserAccessToken("token", "account-object-id")

        repository.storeUserAccessToken(null, null)

        assertFalse(repository.canManageLists())
        assertNull(repository.getUserAccessToken())
    }

    @Test
    fun `canManageListsFlow follows login and logout`() = runTest {
        repository.canManageListsFlow().test {
            assertFalse(awaitItem())

            repository.storeUserAccessToken("token", "account-object-id")
            assertTrue(awaitItem())

            // An unrelated preference change must not re-emit.
            repository.storeSessionId("session")

            repository.storeUserAccessToken(null, null)
            assertFalse(awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `list calls are made with the stored token and account id`() {
        repository.storeUserAccessToken("token", "account-object-id")
        val response = flowOf(TmdbListsResponse())
        every { accountApiHelper.getLists("token", "account-object-id", 1) } returns response

        assertEquals(response, repository.getLists())
        verify { accountApiHelper.getLists("token", "account-object-id", 1) }
    }

    @Test
    fun `a list call without list access fails loudly instead of silently doing nothing`() {
        assertThrows(IllegalStateException::class.java) { repository.getLists() }
        assertThrows(IllegalStateException::class.java) { repository.addToList(1, 550, isTv = false) }
    }
}
