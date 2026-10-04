package tech.salroid.filmy.ui.home

import android.content.Context
import androidx.room.withTransaction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import tech.salroid.filmy.data.local.db.FilmyDatabase
import tech.salroid.filmy.data.local.db.dao.MovieDetailsDao
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.network.MoviesApiHelper
import tech.salroid.filmy.ui.widget.WidgetRefresher

class MoviesRepositoryTest {

    private lateinit var filmyDatabase: FilmyDatabase
    private lateinit var movieDetailsDao: MovieDetailsDao
    private lateinit var moviesApiHelper: MoviesApiHelper
    private lateinit var context: Context
    private lateinit var repository: MoviesRepository

    @Before
    fun setUp() {
        filmyDatabase = mockk(relaxed = true)
        movieDetailsDao = mockk(relaxed = true)
        moviesApiHelper = mockk(relaxed = true)
        context = mockk(relaxed = true)

        every { filmyDatabase.movieDetailsDao() } returns movieDetailsDao
        every { filmyDatabase.runInTransaction(any()) } answers {
            firstArg<Runnable>().run()
        }

        mockkStatic("androidx.room.RoomDatabaseKt")
        val blockSlot = slot<suspend () -> Any?>()
        coEvery { filmyDatabase.withTransaction(capture(blockSlot)) } coAnswers {
            blockSlot.captured.invoke()
        }

        // MoviesRepository fires widget refreshes on its own internal, non-injectable
        // CoroutineScope - real Glance/AppWidgetManager calls aren't mockable here, and
        // letting that coroutine throw pollutes later tests' coroutine scopes. Mock the
        // object directly instead of asserting on this fire-and-forget side effect.
        mockkObject(WidgetRefresher)
        coEvery { WidgetRefresher.refresh(any()) } returns Unit

        repository = MoviesRepository(filmyDatabase, moviesApiHelper, context)
    }

    @After
    fun tearDown() {
        unmockkStatic("androidx.room.RoomDatabaseKt")
        unmockkObject(WidgetRefresher)
    }

    @Test
    fun `addMovieDetailsToLocal inserts a new row when none exists`() {
        every { movieDetailsDao.getDetailsOfType(1, 0) } returns null
        val details = MovieDetails(id = 1, type = 0, watched = true)

        repository.addMovieDetailsToLocal(details)

        verify(exactly = 1) { movieDetailsDao.insert(details) }
        verify(exactly = 0) { movieDetailsDao.updateDetails(any()) }
    }

    @Test
    fun `addMovieDetailsToLocal updates the existing row in place instead of replacing it`() {
        val existing = MovieDetails(id = 1, type = 0, watched = false, title = "Old title")
        every { movieDetailsDao.getDetailsOfType(1, 0) } returns existing
        val updated = existing.copy(title = "New title", watched = true)

        repository.addMovieDetailsToLocal(updated)

        verify(exactly = 1) { movieDetailsDao.updateDetails(updated) }
        verify(exactly = 0) { movieDetailsDao.insert(any()) }
    }

    @Test
    fun `rating an already-watched title refreshes the widgets`() {
        // To Rate lists watched titles without a rating, so a rating changes it
        // even though watched and watchlist stay the same.
        val existing = MovieDetails(id = 1, type = 0, watched = true, userRating = null)
        every { movieDetailsDao.getDetailsOfType(1, 0) } returns existing

        repository.addMovieDetailsToLocal(existing.copy(userRating = 8f))

        coVerify(timeout = 1000, exactly = 1) { WidgetRefresher.refresh(any()) }
    }

    @Test
    fun `a metadata-only save leaves the widgets alone`() {
        val existing = MovieDetails(id = 1, type = 0, watched = true, userRating = 8f, title = "Old title")
        every { movieDetailsDao.getDetailsOfType(1, 0) } returns existing

        repository.addMovieDetailsToLocal(existing.copy(title = "New title"))

        verify(timeout = 1000) { movieDetailsDao.updateDetails(any()) }
        coVerify(exactly = 0) { WidgetRefresher.refresh(any()) }
    }

    @Test
    fun `getMovieDetailsFromLocal delegates straight to the DAO`() {
        val details = MovieDetails(id = 42, type = 1)
        every { movieDetailsDao.getDetailsOfType(42, 1) } returns details

        val result = repository.getMovieDetailsFromLocal(42, 1)

        assertEquals(details, result)
    }

    @Test
    fun `getMovieDetailsFromLocal returns null when nothing is saved`() {
        every { movieDetailsDao.getDetailsOfType(99, 0) } returns null

        assertNull(repository.getMovieDetailsFromLocal(99, 0))
    }

    @Test
    fun `deleteMovieDetailsFromLocal delegates to the DAO`() {
        val details = MovieDetails(id = 7, type = 0)

        repository.deleteMovieDetailsFromLocal(details)

        verify(exactly = 1) { movieDetailsDao.delete(details) }
    }

    @Test
    fun `saveMovieDetailsBatch does nothing for an empty list`() = runTest {
        repository.saveMovieDetailsBatch(emptyList())

        coVerify(exactly = 0) { filmyDatabase.withTransaction(any<suspend () -> Any?>()) }
        verify(exactly = 0) { movieDetailsDao.insert(any()) }
        verify(exactly = 0) { movieDetailsDao.updateDetails(any()) }
    }

    @Test
    fun `saveMovieDetailsBatch upserts every item inside a single transaction`() = runTest {
        every { movieDetailsDao.getDetailsOfType(1, 0) } returns null
        val existing2 = MovieDetails(id = 2, type = 0, watched = false)
        every { movieDetailsDao.getDetailsOfType(2, 0) } returns existing2

        val newItem = MovieDetails(id = 1, type = 0, watched = true)
        val updatedItem = existing2.copy(watched = true)

        repository.saveMovieDetailsBatch(listOf(newItem, updatedItem))

        verify(exactly = 1) { movieDetailsDao.insert(newItem) }
        verify(exactly = 1) { movieDetailsDao.updateDetails(updatedItem) }
    }
}
