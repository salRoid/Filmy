package tech.salroid.filmy.ui.shows

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
import tech.salroid.filmy.data.local.db.entity.MovieDetails
import tech.salroid.filmy.data.local.model.TvShow
import tech.salroid.filmy.data.model.TvShowPreview
import tech.salroid.filmy.ui.home.AccountSyncRepository
import tech.salroid.filmy.ui.home.MoviesRepository

// See MoviesViewModelTest for why paging is verified via repository calls rather
// than asSnapshot() (cachedIn(viewModelScope) hangs indefinitely there).
class ShowsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var accountSyncRepository: AccountSyncRepository
    private lateinit var viewModel: ShowsViewModel

    @Before
    fun setUp() {
        moviesRepository = mockk()
        accountSyncRepository = mockk()
        viewModel = ShowsViewModel(
            moviesRepository,
            TvShowsPreviewMapper(),
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
    fun `showsPagingData fetches the trending category by default`() = runTest(mainDispatcherRule.testDispatcher) {
        every { moviesRepository.getTvShows("day", true) } returns flowOf(PagingData.empty())

        viewModel.showsPagingData.onEach { }.launchIn(backgroundScope)

        verify(timeout = 1000) { moviesRepository.getTvShows("day", true) }
    }

    @Test
    fun `onCategorySelected switches the fetched category`() = runTest(mainDispatcherRule.testDispatcher) {
        every { moviesRepository.getTvShows("day", true) } returns flowOf(PagingData.empty())
        every { moviesRepository.getTvShows("on_the_air", false) } returns flowOf(PagingData.empty())

        viewModel.showsPagingData.onEach { }.launchIn(backgroundScope)
        viewModel.onCategorySelected(TvShow.ShowType.ON_TV)

        verify(timeout = 1000) { moviesRepository.getTvShows("on_the_air", false) }
    }

    @Test
    fun `TvShowsPreviewMapper maps a TvShow into a TvShowPreview`() {
        val tvShow = TvShow(id = 1, name = "A Show", posterPath = "/p.jpg", firstAirDate = "2024-01-15")

        val preview = TvShowsPreviewMapper().map(tvShow)

        assertEquals(1, preview.id)
        assertEquals("A Show", preview.title)
        assertTrue(preview.posterUrl.endsWith("/p.jpg"))
    }

    @Test
    fun `getQuickActionState reflects the local row`() = runTest(mainDispatcherRule.testDispatcher) {
        val preview = TvShowPreview(id = 1, title = "A Show", posterUrl = "", firstAirReadableDate = "")
        every { moviesRepository.getMovieDetailsFromLocal(1, 1) } returns
            MovieDetails(id = 1, type = 1, watched = false, watchlist = true)

        val state = viewModel.getQuickActionState(preview)

        assertEquals(false, state.isWatched)
        assertEquals(true, state.isWatchlisted)
    }

    @Test
    fun `quickToggleWatchlist flips watchlist and keeps it on push success`() = runTest(mainDispatcherRule.testDispatcher) {
        val preview = TvShowPreview(
            id = 1,
            title = "A Show",
            posterUrl = "https://image.tmdb.org/t/p/w500/p.jpg",
            firstAirReadableDate = ""
        )
        every { moviesRepository.getMovieDetailsFromLocal(1, 1) } returns null
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(any(), any()) } returns true

        viewModel.quickToggleWatchlist(preview)

        val expected = MovieDetails(id = 1, type = 1, title = "A Show", posterPath = "/p.jpg", watchlist = true)
        verify(timeout = 1000) { moviesRepository.addMovieDetailsToLocal(expected) }
    }

    @Test
    fun `quickToggleWatched rolls back when the push fails`() = runTest(mainDispatcherRule.testDispatcher) {
        val preview = TvShowPreview(
            id = 1,
            title = "A Show",
            posterUrl = "https://image.tmdb.org/t/p/w500/p.jpg",
            firstAirReadableDate = ""
        )
        val existing = MovieDetails(id = 1, type = 1, title = "A Show", posterPath = "/p.jpg", watched = false)
        every { moviesRepository.getMovieDetailsFromLocal(1, 1) } returns existing
        every { moviesRepository.addMovieDetailsToLocal(any()) } returns Unit
        coEvery { accountSyncRepository.pushItemState(any(), any()) } returns false

        viewModel.quickToggleWatched(preview)

        verify(timeout = 1000) { moviesRepository.addMovieDetailsToLocal(existing) }
        verify { moviesRepository.addMovieDetailsToLocal(existing.copy(watched = true)) }
    }
}
