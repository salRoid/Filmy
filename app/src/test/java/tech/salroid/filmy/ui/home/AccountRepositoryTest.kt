package tech.salroid.filmy.ui.home

import android.content.SharedPreferences
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.local.db.dao.AccountDao
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.network.AccountApiHelper
import tech.salroid.filmy.utility.PreferenceHelper.SESSION_ID

class AccountRepositoryTest {

    private lateinit var appPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var filmyDatabase: FilmyDatabase
    private lateinit var accountDao: AccountDao
    private lateinit var accountApiHelper: AccountApiHelper
    private lateinit var repository: AccountRepository

    @Before
    fun setUp() {
        appPref = mockk()
        editor = mockk(relaxed = true)
        every { appPref.edit() } returns editor
        every { editor.putString(any(), any()) } returns editor

        filmyDatabase = mockk()
        accountDao = mockk(relaxed = true)
        every { filmyDatabase.accountDao() } returns accountDao

        accountApiHelper = mockk()

        repository = AccountRepository(appPref, filmyDatabase, accountApiHelper)
    }

    @Test
    fun `isLoggedIn is true when a session id is stored`() {
        every { appPref.getString(SESSION_ID, null) } returns "session-123"

        assertTrue(repository.isLoggedIn())
    }

    @Test
    fun `isLoggedIn is false when no session id is stored`() {
        every { appPref.getString(SESSION_ID, null) } returns null

        assertFalse(repository.isLoggedIn())
    }

    @Test
    fun `getSessionIdFromPref reads the stored session id`() {
        every { appPref.getString(SESSION_ID, null) } returns "abc"

        assertEquals("abc", repository.getSessionIdFromPref())
    }

    @Test
    fun `storeSessionId writes through the preferences editor`() {
        repository.storeSessionId("new-session")

        verify(exactly = 1) { editor.putString(SESSION_ID, "new-session") }
        verify(exactly = 1) { editor.apply() }
    }

    @Test
    fun `storeSessionId can clear the session id with null`() {
        repository.storeSessionId(null)

        verify(exactly = 1) { editor.putString(SESSION_ID, null) }
    }

    @Test
    fun `saveProfileToLocal replaces any existing row (delete then insert)`() {
        val profile = Profile(id = 1, name = "Test User")

        repository.saveProfileToLocal(profile)

        verify(exactly = 1) { accountDao.delete(profile) }
        verify(exactly = 1) { accountDao.insert(profile) }
    }

    @Test
    fun `clearProfile delegates to the DAO`() {
        every { accountDao.deleteAll() } returns 1

        val result = repository.clearProfile()

        assertEquals(1, result)
        verify(exactly = 1) { accountDao.deleteAll() }
    }

    @Test
    fun `getProfileFromLocal returns the first stored profile`() {
        val profile = Profile(id = 5, name = "Someone")
        every { accountDao.getProfile() } returns listOf(profile)

        assertEquals(profile, repository.getProfileFromLocal())
    }

    @Test
    fun `getProfileFromLocal returns null when nothing is stored`() {
        every { accountDao.getProfile() } returns emptyList()

        assertNull(repository.getProfileFromLocal())
    }
}
