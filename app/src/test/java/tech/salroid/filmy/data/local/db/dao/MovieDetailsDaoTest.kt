package tech.salroid.filmy.data.local.db.dao

import app.cash.turbine.test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.local.db.createTestDatabase
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.Genre
import tech.salroid.filmy.data.local.model.Trailers
import tech.salroid.filmy.data.local.model.Youtube

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MovieDetailsDaoTest {

    private lateinit var db: FilmyDatabase
    private lateinit var dao: MovieDetailsDao

    @Before
    fun setUp() {
        db = createTestDatabase()
        dao = db.movieDetailsDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `insert then getDetailsOfType round-trips complex TypeConverter-backed fields`() {
        val genres = arrayListOf(Genre(id = 28, name = "Action"), Genre(id = 12, name = "Adventure"))
        val trailers = Trailers(youtube = arrayListOf(Youtube(name = "Trailer 1", source = "abc123")))
        dao.insert(MovieDetails(id = 1, type = 0, title = "A Movie", genres = genres, trailers = trailers))

        val stored = dao.getDetailsOfType(1, 0)

        assertEquals(genres, stored?.genres)
        assertEquals(trailers, stored?.trailers)
    }

    @Test
    fun `insert then getDetailsOfType handles null complex fields`() {
        dao.insert(MovieDetails(id = 1, type = 0, title = "A Movie", trailers = null, videos = null))

        val stored = dao.getDetailsOfType(1, 0)

        assertNull(stored?.trailers)
        assertNull(stored?.videos)
        assertEquals(arrayListOf<Genre>(), stored?.genres)
    }

    @Test
    fun `getDetailsOfType returns null when nothing is stored for that id-type pair`() {
        assertNull(dao.getDetailsOfType(99, 0))
    }

    @Test
    fun `id and type together form the primary key`() {
        // Same TMDB id for a movie and a tv show must be tracked independently.
        dao.insert(MovieDetails(id = 1, type = 0, title = "Movie version"))
        dao.insert(MovieDetails(id = 1, type = 1, title = "TV version"))

        assertEquals("Movie version", dao.getDetailsOfType(1, 0)?.title)
        assertEquals("TV version", dao.getDetailsOfType(1, 1)?.title)
    }

    @Test
    fun `updateDetails updates the row in place rather than inserting a duplicate`() {
        dao.insert(MovieDetails(id = 1, type = 0, title = "Original"))

        val rowsUpdated = dao.updateDetails(MovieDetails(id = 1, type = 0, title = "Updated"))

        assertEquals(1, rowsUpdated)
        assertEquals(1, dao.getAllDetails().size)
        assertEquals("Updated", dao.getDetailsOfType(1, 0)?.title)
    }

    @Test
    fun `delete removes just that row`() {
        val toDelete = MovieDetails(id = 1, type = 0, title = "Delete me")
        dao.insert(toDelete)
        dao.insert(MovieDetails(id = 2, type = 0, title = "Keep me"))

        dao.delete(toDelete)

        assertEquals(listOf("Keep me"), dao.getAllDetails().map { it.title })
    }

    @Test
    fun `getAllWatched, getAllWatchlist and getAllRated filter correctly`() = runTest {
        dao.insert(MovieDetails(id = 1, type = 0, title = "Watched", watched = true))
        dao.insert(MovieDetails(id = 2, type = 0, title = "Watchlisted", watchlist = true))
        dao.insert(MovieDetails(id = 3, type = 0, title = "Rated", userRating = 8f))
        dao.insert(MovieDetails(id = 4, type = 0, title = "None of the above"))

        assertEquals(listOf("Watched"), dao.getAllWatched().first().map { it.title })
        assertEquals(listOf("Watchlisted"), dao.getAllWatchlist().first().map { it.title })
        assertEquals(listOf("Rated"), dao.getAllRated().first().map { it.title })
    }

    @Test
    fun `getWatchedUnrated excludes watched items that already have a rating`() = runTest {
        dao.insert(MovieDetails(id = 1, type = 0, title = "Watched unrated", watched = true, userRating = null))
        dao.insert(MovieDetails(id = 2, type = 0, title = "Watched and rated", watched = true, userRating = 7f))
        dao.insert(MovieDetails(id = 3, type = 0, title = "Not watched", watched = false))

        val titles = dao.getWatchedUnrated().first().map { it.title }

        assertEquals(listOf("Watched unrated"), titles)
    }

    @Test
    fun `getDetailsFlow reacts to inserts and updates for that id-type pair`() = runTest {
        dao.getDetailsFlow(1, 0).test {
            assertNull(awaitItem())

            dao.insert(MovieDetails(id = 1, type = 0, title = "First save", watched = false))
            assertEquals("First save", awaitItem()?.title)

            dao.updateDetails(MovieDetails(id = 1, type = 0, title = "First save", watched = true))
            val updated = awaitItem()
            assertEquals(true, updated?.watched)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getDetailsFlow does not react to changes for a different id-type pair`() = runTest {
        dao.getDetailsFlow(1, 0).test {
            assertNull(awaitItem())

            dao.insert(MovieDetails(id = 2, type = 0, title = "Different movie"))
            dao.insert(MovieDetails(id = 1, type = 1, title = "Same id, different type"))

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
