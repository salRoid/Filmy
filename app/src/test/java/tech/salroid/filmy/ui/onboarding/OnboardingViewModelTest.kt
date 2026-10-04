package tech.salroid.filmy.ui.onboarding

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.cancelScopeAndJoin
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.model.MoviesResponse
import tech.salroid.filmy.ui.home.MoviesRepository

class OnboardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private var viewModel: OnboardingViewModel? = null

    // detectedCountry isn't asserted here: it's wired to IpGeolocation, a
    // real network call with no injectable seam, so its outcome can't be
    // controlled from a unit test. It's best-effort by design (see the
    // class doc on OnboardingViewModel) and never blocks posterUrls.
    //
    // That real call outlives the test method (it hops to the real
    // Dispatchers.IO, decoupled from runTest's virtual time), so it's
    // cancelled in tearDown - otherwise it tries to resume on Main after
    // MainDispatcherRule has already reset it, logging a spurious
    // "main looper is not available" crash from an unrelated later test.
    @After
    fun tearDown() {
        viewModel?.cancelScopeAndJoin()
    }

    @Test
    fun `posterUrls is populated from today's trending movies, base-URL prefixed and nulls dropped`() = runTest {
        val movies = listOf(
            Movie(id = 1, posterPath = "/a.jpg"),
            Movie(id = 2, posterPath = "/b.jpg"),
            Movie(id = 3, posterPath = null)
        )
        val moviesRepository = mockk<MoviesRepository>()
        every { moviesRepository.getMoviesFlow(type = "day", isTrending = true) } returns
            flowOf(Result.success(MoviesResponse(results = movies)))

        viewModel = OnboardingViewModel(moviesRepository)

        viewModel!!.posterUrls.test {
            val urls = awaitItem()
            assertEquals(
                setOf("https://image.tmdb.org/t/p/w500/a.jpg", "https://image.tmdb.org/t/p/w500/b.jpg"),
                urls.toSet()
            )
            assertEquals(2, urls.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `posterUrls stays empty when the repository call fails`() = runTest {
        val moviesRepository = mockk<MoviesRepository>()
        every { moviesRepository.getMoviesFlow(type = "day", isTrending = true) } returns
            flowOf(Result.failure(RuntimeException("network error")))

        viewModel = OnboardingViewModel(moviesRepository)

        viewModel!!.posterUrls.test {
            assertEquals(emptyList<String>(), awaitItem())
            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
