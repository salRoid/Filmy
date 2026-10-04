package tech.salroid.filmy.data.local.db.dao

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.local.db.createTestDatabase
import tech.salroid.filmy.data.local.db.entity.Profile

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AccountDaoTest {

    private lateinit var db: FilmyDatabase
    private lateinit var dao: AccountDao

    @Before
    fun setUp() {
        db = createTestDatabase()
        dao = db.accountDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `insert then getProfile returns the stored profile`() {
        dao.insert(Profile(id = 1, name = "Someone", username = "someone"))

        val profiles = dao.getProfile()

        assertEquals(1, profiles.size)
        assertEquals("Someone", profiles.first().name)
    }

    @Test
    fun `insert replaces an existing profile with the same id`() {
        dao.insert(Profile(id = 1, name = "Old name"))
        dao.insert(Profile(id = 1, name = "New name"))

        val profiles = dao.getProfile()

        assertEquals(1, profiles.size)
        assertEquals("New name", profiles.first().name)
    }

    @Test
    fun `delete removes the profile`() {
        val profile = Profile(id = 1, name = "Someone")
        dao.insert(profile)

        dao.delete(profile)

        assertTrue(dao.getProfile().isEmpty())
    }

    @Test
    fun `deleteAll clears every stored profile`() {
        dao.insert(Profile(id = 1, name = "A"))

        val deletedCount = dao.deleteAll()

        assertEquals(1, deletedCount)
        assertTrue(dao.getProfile().isEmpty())
    }

    @Test
    fun `getProfileFlow reacts to inserts made after subscription`() = runTest {
        dao.getProfileFlow().test {
            assertEquals(emptyList<Profile>(), awaitItem())

            dao.insert(Profile(id = 1, name = "Someone"))

            val updated = awaitItem()
            assertEquals(1, updated.size)
            assertEquals("Someone", updated.first().name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getProfileFlow reacts to deletes`() = runTest {
        val profile = Profile(id = 1, name = "Someone")
        dao.insert(profile)

        dao.getProfileFlow().test {
            assertEquals(listOf(profile), awaitItem())

            dao.delete(profile)

            assertEquals(emptyList<Profile>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
