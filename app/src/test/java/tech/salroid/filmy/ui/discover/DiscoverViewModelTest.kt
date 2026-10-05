package tech.salroid.filmy.ui.discover

import androidx.paging.PagingData
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
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.cancelScopeAndJoin
import tech.salroid.filmy.data.local.model.Genre
import tech.salroid.filmy.data.local.model.discover.DiscoverFilters
import tech.salroid.filmy.data.local.model.discover.GenreResponse
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.ui.movies.MoviePreviewMapper
import tech.salroid.filmy.ui.shows.TvShowsPreviewMapper

// See MoviesViewModelTest for why paging is verified via repository calls rather
// than asSnapshot().
class DiscoverViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var viewModel: DiscoverViewModel

    @Before
    fun setUp() {
        moviesRepository = mockk()
        viewModel = DiscoverViewModel(moviesRepository, MoviePreviewMapper(), TvShowsPreviewMapper())
    }

    @After
    fun tearDown() {
        viewModel.cancelScopeAndJoin()
    }

    @Test
    fun `initialize fetches movie genres when isTv is false`() = runTest(mainDispatcherRule.testDispatcher) {
        val genre = Genre(id = 28, name = "Action")
        every { moviesRepository.getMovieGenres() } returns flowOf(GenreResponse(genres = listOf(genre)))

        viewModel.initialize(isTv = false)

        verify(timeout = 1000) { moviesRepository.getMovieGenres() }
        assertEquals(listOf(genre), viewModel.genres.value)
        assertEquals(false, viewModel.isTv)
    }

    @Test
    fun `initialize fetches tv genres when isTv is true`() = runTest(mainDispatcherRule.testDispatcher) {
        val genre = Genre(id = 18, name = "Drama")
        every { moviesRepository.getTvGenres() } returns flowOf(GenreResponse(genres = listOf(genre)))

        viewModel.initialize(isTv = true)

        verify(timeout = 1000) { moviesRepository.getTvGenres() }
        assertEquals(listOf(genre), viewModel.genres.value)
        assertTrue(viewModel.isTv)
    }

    @Test
    fun `initialize only runs once`() = runTest(mainDispatcherRule.testDispatcher) {
        every { moviesRepository.getMovieGenres() } returns flowOf(GenreResponse())

        viewModel.initialize(isTv = false)
        viewModel.initialize(isTv = true)

        verify(timeout = 1000) { moviesRepository.getMovieGenres() }
        verify(exactly = 0) { moviesRepository.getTvGenres() }
        assertEquals(false, viewModel.isTv)
    }

    @Test
    fun `initialize seeds filters from a keyword when provided`() = runTest(mainDispatcherRule.testDispatcher) {
        every { moviesRepository.getMovieGenres() } returns flowOf(GenreResponse())

        viewModel.initialize(isTv = false, keywordId = 5, keywordName = "heist")

        assertEquals(5, viewModel.filters.value.keywordId)
        assertEquals("heist", viewModel.filters.value.keywordName)
    }

    @Test
    fun `applyFilters updates filters and re-triggers the movies paging source`() = runTest(mainDispatcherRule.testDispatcher) {
        val initialFilters = DiscoverFilters()
        val newFilters = DiscoverFilters(year = 2020)
        every { moviesRepository.discoverMovies(initialFilters) } returns flowOf(PagingData.empty())
        every { moviesRepository.discoverMovies(newFilters) } returns flowOf(PagingData.empty())

        viewModel.moviesResults.onEach { }.launchIn(backgroundScope)
        viewModel.applyFilters(newFilters)

        verify(timeout = 1000) { moviesRepository.discoverMovies(newFilters) }
        assertEquals(newFilters, viewModel.filters.value)
    }

    @Test
    fun `showsResults fetches from discoverTv`() = runTest(mainDispatcherRule.testDispatcher) {
        every { moviesRepository.discoverTv(DiscoverFilters()) } returns flowOf(PagingData.empty())

        viewModel.showsResults.onEach { }.launchIn(backgroundScope)

        verify(timeout = 1000) { moviesRepository.discoverTv(DiscoverFilters()) }
    }
}
