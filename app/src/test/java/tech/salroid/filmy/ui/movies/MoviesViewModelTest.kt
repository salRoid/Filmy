package tech.salroid.filmy.ui.movies

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.FakeSharedPreferences
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.cancelScopeAndJoin
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.model.MoviePreview
import tech.salroid.filmy.ui.home.AccountSyncRepository
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.utility.ApiLanguage

// moviesPagingData chains combine()+flatMapLatest()+map()+cachedIn(viewModelScope) -
// cachedIn's multicasting is well documented as hard to drive to completion with
// Paging's asSnapshot() in a pure JVM test (it hung indefinitely here even with a
// shared test scheduler). Verifying that selecting a category triggers the right
// repository call - via a lightweight collector, not asSnapshot() - captures the
// same behavior without that machinery; MoviePreviewMapper's own transform is
// covered directly and separately below.
class MoviesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var accountSyncRepository: AccountSyncRepository
    private lateinit var viewModel: MoviesViewModel

    @Before
    fun setUp() {
        moviesRepository = mockk()
        accountSyncRepository = mockk()
        viewModel = MoviesViewModel(
            moviesRepository,
            MoviePreviewMapper(),
            accountSyncRepository,
            FakeSharedPreferences()
        )
    }

    // quickToggleWatched/quickToggleWatchlist launch with Dispatchers.IO -
    // a real dispatcher hop outside the test's virtual time - so a
    // just-finished test's coroutine can still be winding down when the
    // next test's MainDispatcherRule resets Main. Cancelled here so it
    // doesn't try to resume on a torn-down Main and crash asynchronously
    // during a later test.
    @After
    fun tearDown() {
        viewModel.cancelScopeAndJoin()
    }

    @Test
    fun `moviesPagingData fetches the trending category by default`() = runTest(mainDispatcherRule.testDispatcher) {
        every { moviesRepository.getMovies("day", true) } returns flowOf(PagingData.empty())

        viewModel.moviesPagingData.onEach { }.launchIn(backgroundScope)

        verify(timeout = 1000) { moviesRepository.getMovies("day", true) }
    }

    @Test
    fun `onCategorySelected switches the fetched category`() = runTest(mainDispatcherRule.testDispatcher) {
        every { moviesRepository.getMovies("day", true) } returns flowOf(PagingData.empty())
        every { moviesRepository.getMovies("popular", false) } returns flowOf(PagingData.empty())

        viewModel.moviesPagingData.onEach { }.launchIn(backgroundScope)
        viewModel.onCategorySelected(Movie.MovieType.POPULAR)

        verify(timeout = 1000) { moviesRepository.getMovies("popular", false) }
    }

    @Test
    fun `moviesPagingData refetches when the API language changes`() = runTest(mainDispatcherRule.testDispatcher) {
        every { moviesRepository.getMovies("day", true) } returns flowOf(PagingData.empty())
        ApiLanguage.update("en-US")

        viewModel.moviesPagingData.onEach { }.launchIn(backgroundScope)
        verify(timeout = 1000, exactly = 1) { moviesRepository.getMovies("day", true) }
        ApiLanguage.update("hi-IN")

        verify(timeout = 1000, exactly = 2) { moviesRepository.getMovies("day", true) }
    }

    @Test
    fun `MoviePreviewMapper maps a Movie into a MoviePreview`() {
        val movie = Movie(id = 1, title = "A Movie", posterPath = "/p.jpg", releaseDate = "2024-01-15")

        val preview = MoviePreviewMapper().map(movie)

        assertEquals(1, preview.id)
        assertEquals("A Movie", preview.title)
        assertTrue(preview.posterUrl.endsWith("/p.jpg"))
    }

    @Test
    fun `getQuickActionState reflects the local row`() = runTest(mainDispatcherRule.testDispatcher) {
        val preview = MoviePreview(id = 1, title = "A Movie", posterUrl = "", readableReleaseDate = "")
        every { moviesRepository.getMovieDetailsFromLocal(1, 0) } returns
            MovieDetails(id = 1, type = 0, watched = true, watchlist = false)

        val state = viewModel.getQuickActionState(preview)

        assertEquals(true, state.isWatched)
        assertEquals(false, state.isWatchlisted)
    }

    @Test
    fun `getQuickActionState defaults to false when nothing is saved locally`() = runTest(mainDispatcherRule.testDispatcher) {
        val preview = MoviePreview(id = 9, title = "Unknown", posterUrl = "", readableReleaseDate = "")
        every { moviesRepository.getMovieDetailsFromLocal(9, 0) } returns null

        val state = viewModel.getQuickActionState(preview)

        assertEquals(false, state.isWatched)
        assertEquals(false, state.isWatchlisted)
    }

    @Test
    fun `quickToggleWatchlist flips watchlist and keeps it on push success`() = runTest(mainDispatcherRule.testDispatcher) {
        val preview = MoviePreview(
            id = 1,
            title = "A Movie",
            posterUrl = "https://image.tmdb.org/t/p/w500/p.jpg",
            readableReleaseDate = ""
        )
        every { moviesRepository.getMovieDetailsFromLocal(1, 0) } returns null
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(any(), any()) } returns true

        viewModel.quickToggleWatchlist(preview)

        val expected = MovieDetails(id = 1, title = "A Movie", posterPath = "/p.jpg", watchlist = true)
        verify(timeout = 1000) { moviesRepository.addMovieDetailsToLocal(expected) }
    }

    @Test
    fun `quickToggleWatched rolls back when the push fails`() = runTest(mainDispatcherRule.testDispatcher) {
        val preview = MoviePreview(
            id = 1,
            title = "A Movie",
            posterUrl = "https://image.tmdb.org/t/p/w500/p.jpg",
            readableReleaseDate = ""
        )
        val existing = MovieDetails(id = 1, title = "A Movie", posterPath = "/p.jpg", watched = false)
        every { moviesRepository.getMovieDetailsFromLocal(1, 0) } returns existing
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(any(), any()) } returns false

        viewModel.quickToggleWatched(preview)

        verify(timeout = 1000) { moviesRepository.addMovieDetailsToLocal(existing) }
        verify { moviesRepository.addMovieDetailsToLocal(existing.copy(watched = true)) }
    }
}
