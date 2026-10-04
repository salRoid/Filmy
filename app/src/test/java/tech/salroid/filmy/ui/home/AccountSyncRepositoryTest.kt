package tech.salroid.filmy.ui.home

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.data.local.model.MoviesResponse
import tech.salroid.filmy.data.local.model.TvShow
import tech.salroid.filmy.data.local.model.TvShowResponse
import tech.salroid.filmy.data.local.model.account.RatedResponse
import tech.salroid.filmy.data.local.model.account.TmdbStatusResponse
import tech.salroid.filmy.data.local.model.tv.TvDetails

private const val ACCOUNT_ID = 1
private const val SESSION_ID = "session"

class AccountSyncRepositoryTest {

    private lateinit var accountRepository: AccountRepository
    private lateinit var moviesRepository: MoviesRepository
    private lateinit var repository: AccountSyncRepository

    @Before
    fun setUp() {
        accountRepository = mockk()
        moviesRepository = mockk()
        repository = AccountSyncRepository(accountRepository, moviesRepository)

        // Empty-sync defaults: everything used by syncIfNeeded()'s pull/push path is
        // stubbed to "nothing to do" so individual tests only need to override the
        // specific calls relevant to what they're asserting.
        every { accountRepository.isLoggedIn() } returns true
        every { accountRepository.getSessionIdFromPref() } returns SESSION_ID
        every { accountRepository.getProfileFromLocal() } returns Profile(id = ACCOUNT_ID)

        every { accountRepository.getFavoriteMovies(ACCOUNT_ID, SESSION_ID, any()) } returns
            flowOf(MoviesResponse(results = emptyList(), totalPages = 1))
        every { accountRepository.getWatchlistMovies(ACCOUNT_ID, SESSION_ID, any()) } returns
            flowOf(MoviesResponse(results = emptyList(), totalPages = 1))
        every { accountRepository.getFavoriteTv(ACCOUNT_ID, SESSION_ID, any()) } returns
            flowOf(TvShowResponse(results = arrayListOf(), totalPages = 1))
        every { accountRepository.getWatchlistTv(ACCOUNT_ID, SESSION_ID, any()) } returns
            flowOf(TvShowResponse(results = arrayListOf(), totalPages = 1))
        every { accountRepository.getRatedMovies(ACCOUNT_ID, SESSION_ID, any()) } returns
            flowOf(RatedResponse(results = emptyList(), totalPages = 1))
        every { accountRepository.getRatedTv(ACCOUNT_ID, SESSION_ID, any()) } returns
            flowOf(RatedResponse(results = emptyList(), totalPages = 1))

        every { moviesRepository.getWatched() } returns flowOf(emptyList())
        every { moviesRepository.getWatchlist() } returns flowOf(emptyList())
        every { moviesRepository.getRated() } returns flowOf(emptyList())
        coEvery { moviesRepository.saveMovieDetailsBatch(any()) } returns Unit
    }

    // --- syncIfNeeded guards ---

    @Test
    fun `syncIfNeeded does nothing when the user is not logged in`() = runTest {
        every { accountRepository.isLoggedIn() } returns false

        repository.syncIfNeeded()

        coVerify(exactly = 0) { moviesRepository.saveMovieDetailsBatch(any()) }
    }

    @Test
    fun `syncIfNeeded does nothing when there is no session id`() = runTest {
        every { accountRepository.getSessionIdFromPref() } returns null

        repository.syncIfNeeded()

        coVerify(exactly = 0) { moviesRepository.saveMovieDetailsBatch(any()) }
    }

    @Test
    fun `syncIfNeeded does nothing when there is no local profile`() = runTest {
        every { accountRepository.getProfileFromLocal() } returns null

        repository.syncIfNeeded()

        coVerify(exactly = 0) { moviesRepository.saveMovieDetailsBatch(any()) }
    }

    @Test
    fun `syncIfNeeded only runs once per process even across repeated calls`() = runTest {
        repository.syncIfNeeded()
        repository.syncIfNeeded()
        repository.syncIfNeeded()

        verify(exactly = 1) { accountRepository.isLoggedIn() }
    }

    @Test
    fun `syncIfNeeded swallows exceptions and still marks the session as synced`() = runTest {
        every { accountRepository.getFavoriteMovies(ACCOUNT_ID, SESSION_ID, any()) } throws RuntimeException("boom")

        repository.syncIfNeeded()
        repository.syncIfNeeded()

        verify(exactly = 1) { accountRepository.isLoggedIn() }
    }

    // --- pull path ---

    @Test
    fun `syncIfNeeded pulls a TMDB favorite movie and saves it locally`() = runTest {
        every { accountRepository.getFavoriteMovies(ACCOUNT_ID, SESSION_ID, any()) } returns
            flowOf(MoviesResponse(results = listOf(Movie(id = 42)), totalPages = 1))
        every { moviesRepository.getMovieDetailsFromLocal(42, 0) } returns null
        coEvery { moviesRepository.getMovieDetailsFromNetwork("42") } returns
            flowOf(MovieDetails(id = 42, type = 0))

        repository.syncIfNeeded()

        coVerify(exactly = 1) {
            moviesRepository.saveMovieDetailsBatch(
                match { list -> list.any { it.id == 42 && it.watched } }
            )
        }
    }

    @Test
    fun `syncIfNeeded skips re-fetching a pulled item that already matches local state`() = runTest {
        every { accountRepository.getFavoriteMovies(ACCOUNT_ID, SESSION_ID, any()) } returns
            flowOf(MoviesResponse(results = listOf(Movie(id = 42)), totalPages = 1))
        every { moviesRepository.getMovieDetailsFromLocal(42, 0) } returns
            MovieDetails(id = 42, type = 0, watched = true, watchlist = false, userRating = null)

        repository.syncIfNeeded()

        coVerify(exactly = 0) { moviesRepository.getMovieDetailsFromNetwork(any()) }
        coVerify(exactly = 1) { moviesRepository.saveMovieDetailsBatch(emptyList()) }
    }

    @Test
    fun `syncIfNeeded pulls a TMDB favorite tv show via the tv-details mapper`() = runTest {
        every { accountRepository.getFavoriteTv(ACCOUNT_ID, SESSION_ID, any()) } returns
            flowOf(TvShowResponse(results = arrayListOf(TvShow(id = 7)), totalPages = 1))
        every { moviesRepository.getMovieDetailsFromLocal(7, 1) } returns null
        coEvery { moviesRepository.getTvShowDetailsFromNetwork("7") } returns
            flowOf(TvDetails(id = 7, name = "A Show"))

        repository.syncIfNeeded()

        coVerify(exactly = 1) {
            moviesRepository.saveMovieDetailsBatch(
                match { list -> list.any { it.id == 7 && it.type == 1 && it.watched } }
            )
        }
    }

    // --- push-local-only path ---

    @Test
    fun `syncIfNeeded pushes a locally-watched movie TMDB does not already have favorited`() = runTest {
        every { moviesRepository.getWatched() } returns
            flowOf(listOf(MovieDetails(id = 99, type = 0, watched = true)))
        coEvery { accountRepository.markFavorite(ACCOUNT_ID, SESSION_ID, "movie", 99, true) } returns
            flowOf(TmdbStatusResponse())

        repository.syncIfNeeded()

        coVerify(exactly = 1) { accountRepository.markFavorite(ACCOUNT_ID, SESSION_ID, "movie", 99, true) }
    }

    @Test
    fun `syncIfNeeded does not re-push an item TMDB already has favorited`() = runTest {
        every { accountRepository.getFavoriteMovies(ACCOUNT_ID, SESSION_ID, any()) } returns
            flowOf(MoviesResponse(results = listOf(Movie(id = 99)), totalPages = 1))
        every { moviesRepository.getMovieDetailsFromLocal(99, 0) } returns
            MovieDetails(id = 99, type = 0, watched = true)
        every { moviesRepository.getWatched() } returns
            flowOf(listOf(MovieDetails(id = 99, type = 0, watched = true)))

        repository.syncIfNeeded()

        coVerify(exactly = 0) { accountRepository.markFavorite(any(), any(), any(), any(), any()) }
    }

    // --- pushItemState ---

    @Test
    fun `pushItemState no-ops as success when logged out`() = runTest {
        every { accountRepository.isLoggedIn() } returns false

        val result = repository.pushItemState(MovieDetails(id = 1))

        assertTrue(result)
        coVerify(exactly = 0) { accountRepository.markFavorite(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `pushItemState pushes only the field that changed`() = runTest {
        val previous = MovieDetails(id = 5, type = 0, watched = true, watchlist = false)
        val updated = previous.copy(watchlist = true)
        coEvery { accountRepository.markWatchlist(ACCOUNT_ID, SESSION_ID, "movie", 5, true) } returns
            flowOf(TmdbStatusResponse())

        val result = repository.pushItemState(updated, previous)

        assertTrue(result)
        coVerify(exactly = 1) { accountRepository.markWatchlist(ACCOUNT_ID, SESSION_ID, "movie", 5, true) }
        // An unrelated favorite request can no longer fail a watchlist toggle.
        coVerify(exactly = 0) { accountRepository.markFavorite(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `pushItemState treats a missing previous row as neither watched nor watchlisted`() = runTest {
        val details = MovieDetails(id = 5, type = 1, watched = true, watchlist = false)
        coEvery { accountRepository.markFavorite(ACCOUNT_ID, SESSION_ID, "tv", 5, true) } returns
            flowOf(TmdbStatusResponse())

        val result = repository.pushItemState(details)

        assertTrue(result)
        coVerify(exactly = 1) { accountRepository.markFavorite(ACCOUNT_ID, SESSION_ID, "tv", 5, true) }
        coVerify(exactly = 0) { accountRepository.markWatchlist(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `pushItemState makes no request when nothing changed`() = runTest {
        val row = MovieDetails(id = 5, type = 0, watched = true, watchlist = true)

        assertTrue(repository.pushItemState(row.copy(title = "Renamed"), previous = row))

        coVerify(exactly = 0) { accountRepository.markFavorite(any(), any(), any(), any(), any()) }
        coVerify(exactly = 0) { accountRepository.markWatchlist(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `pushItemState returns false when the push fails`() = runTest {
        every { accountRepository.markFavorite(any(), any(), any(), any(), any()) } throws RuntimeException("network error")

        val result = repository.pushItemState(MovieDetails(id = 5, type = 0, watched = true))

        assertFalse(result)
    }

    @Test
    fun `pushItemState undoes the half that went through when the other half fails`() = runTest {
        // The widget's "mark watched" changes both: watched on, watchlist off.
        val previous = MovieDetails(id = 5, type = 0, watched = false, watchlist = true)
        val updated = previous.copy(watched = true, watchlist = false)
        coEvery { accountRepository.markFavorite(ACCOUNT_ID, SESSION_ID, "movie", 5, any()) } returns
            flowOf(TmdbStatusResponse())
        every { accountRepository.markWatchlist(any(), any(), any(), any(), any()) } throws RuntimeException("network error")

        val result = repository.pushItemState(updated, previous)

        assertFalse(result)
        // Favorite was applied, then put back so TMDB matches the local rollback.
        coVerifyOrder {
            accountRepository.markFavorite(ACCOUNT_ID, SESSION_ID, "movie", 5, true)
            accountRepository.markFavorite(ACCOUNT_ID, SESSION_ID, "movie", 5, false)
        }
    }

    // --- pushRating ---

    @Test
    fun `pushRating no-ops as success when logged out`() = runTest {
        every { accountRepository.isLoggedIn() } returns false

        assertTrue(repository.pushRating(MovieDetails(id = 1, userRating = 4.5f)))
    }

    @Test
    fun `pushRating deletes the TMDB movie rating when userRating is cleared`() = runTest {
        val details = MovieDetails(id = 10, type = 0, userRating = null)
        coEvery { accountRepository.deleteMovieRating(10, SESSION_ID) } returns flowOf(TmdbStatusResponse())

        val result = repository.pushRating(details)

        assertTrue(result)
        coVerify(exactly = 1) { accountRepository.deleteMovieRating(10, SESSION_ID) }
    }

    @Test
    fun `pushRating rates a tv show when userRating is set`() = runTest {
        val details = MovieDetails(id = 11, type = 1, userRating = 8f)
        coEvery { accountRepository.rateTv(11, SESSION_ID, 8f) } returns flowOf(TmdbStatusResponse())

        val result = repository.pushRating(details)

        assertTrue(result)
        coVerify(exactly = 1) { accountRepository.rateTv(11, SESSION_ID, 8f) }
    }
}
