package tech.salroid.filmy.data.local.db.dao

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.local.db.createTestDatabase
import tech.salroid.filmy.data.local.db.entity.Movie

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MovieDaoTest {

    private lateinit var db: FilmyDatabase
    private lateinit var dao: MovieDao

    @Before
    fun setUp() {
        db = createTestDatabase()
        dao = db.movieDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `insertAll then getAll returns everything inserted`() {
        dao.insertAll(listOf(Movie(id = 1, type = 0, title = "A"), Movie(id = 2, type = 0, title = "B")))

        val all = dao.getAll()

        assertEquals(setOf("A", "B"), all.map { it.title }.toSet())
    }

    @Test
    fun `insertAll replaces an existing row with the same primary key`() {
        dao.insertAll(listOf(Movie(id = 1, type = 0, title = "Original")))
        dao.insertAll(listOf(Movie(id = 1, type = 0, title = "Replaced")))

        val all = dao.getAll()

        assertEquals(1, all.size)
        assertEquals("Replaced", all.first().title)
    }

    @Test
    fun `id and type together form the primary key`() {
        // Same id, different type (e.g. a movie and a show sharing a TMDB id) -
        // must both be kept, not treated as the same row.
        dao.insertAll(listOf(Movie(id = 1, type = 0, title = "Movie"), Movie(id = 1, type = 1, title = "Show")))

        val all = dao.getAll()

        assertEquals(2, all.size)
    }

    @Test
    fun `getAllTrending, getAllUpcoming and getAllInTheaters filter by type`() {
        dao.insertAll(
            listOf(
                Movie(id = 1, type = 0, title = "Trending"),
                Movie(id = 2, type = 1, title = "Upcoming"),
                Movie(id = 3, type = 2, title = "In Theaters")
            )
        )

        assertEquals(listOf("Trending"), dao.getAllTrending().map { it.title })
        assertEquals(listOf("Upcoming"), dao.getAllUpcoming().map { it.title })
        assertEquals(listOf("In Theaters"), dao.getAllInTheaters().map { it.title })
    }

    @Test
    fun `delete removes just that row`() {
        val a = Movie(id = 1, type = 0, title = "A")
        val b = Movie(id = 2, type = 0, title = "B")
        dao.insertAll(listOf(a, b))

        dao.delete(a)

        assertEquals(listOf("B"), dao.getAll().map { it.title })
    }

    @Test
    fun `updateMovie updates an existing row in place`() {
        dao.insertAll(listOf(Movie(id = 1, type = 0, title = "Original", popularity = 1.0)))

        val updated = dao.updateMovie(Movie(id = 1, type = 0, title = "Updated", popularity = 9.0))

        assertEquals(1, updated)
        val stored = dao.getAll().first()
        assertEquals("Updated", stored.title)
        assertEquals(9.0, stored.popularity)
    }

    @Test
    fun `updateMovie is a no-op when the row does not exist`() {
        val updated = dao.updateMovie(Movie(id = 99, type = 0, title = "Ghost"))

        assertEquals(0, updated)
        assertNull(dao.getAll().find { it.id == 99 })
    }
}
