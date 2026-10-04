package tech.salroid.filmy.data.datasource

import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.model.MoviesResponse
import tech.salroid.filmy.data.local.model.PeopleResponse
import tech.salroid.filmy.data.local.model.Person
import tech.salroid.filmy.data.local.model.TvShow
import tech.salroid.filmy.data.local.model.TvShowResponse
import tech.salroid.filmy.data.local.model.discover.DiscoverFilters
import tech.salroid.filmy.data.local.model.discover.DiscoverSort
import tech.salroid.filmy.data.network.MoviesApiService
import java.io.IOException

/**
 * The five TMDB paging sources share one paging contract (first page has no
 * previous key, the last or an empty page has no next key, failures surface
 * as LoadResult.Error) and differ only in which endpoint they call.
 */
class PagingSourcesTest {

    private val api: MoviesApiService = mockk()

    private fun refresh(key: Int? = null) = LoadParams.Refresh(key, loadSize = 20, placeholdersEnabled = false)

    private fun <T : Any> LoadResult<Int, T>.page() = this as LoadResult.Page<Int, T>

    // --- shared paging contract, exercised through MoviesPagingSource ---

    @Test
    fun `the first page has no previous key and points at page two`() = runTest {
        val movies = listOf(Movie(id = 1), Movie(id = 2))
        coEvery { api.getAllMovies("popular", 1) } returns MoviesResponse(results = movies, totalPages = 3)

        val page = MoviesPagingSource(api, "popular").load(refresh()).page()

        assertEquals(movies, page.data)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun `a middle page links to both neighbours`() = runTest {
        coEvery { api.getAllMovies("popular", 2) } returns MoviesResponse(results = listOf(Movie(id = 3)), totalPages = 3)

        val page = MoviesPagingSource(api, "popular").load(refresh(key = 2)).page()

        assertEquals(1, page.prevKey)
        assertEquals(3, page.nextKey)
    }

    @Test
    fun `the last page has no next key`() = runTest {
        coEvery { api.getAllMovies("popular", 3) } returns MoviesResponse(results = listOf(Movie(id = 5)), totalPages = 3)

        assertNull(MoviesPagingSource(api, "popular").load(refresh(key = 3)).page().nextKey)
    }

    @Test
    fun `an empty page ends paging even if more pages are advertised`() = runTest {
        coEvery { api.getAllMovies("popular", 1) } returns MoviesResponse(results = emptyList(), totalPages = 9)

        assertNull(MoviesPagingSource(api, "popular").load(refresh()).page().nextKey)
    }

    @Test
    fun `a failed request becomes a load error`() = runTest {
        val failure = IOException("offline")
        coEvery { api.getAllMovies("popular", 1) } throws failure

        val result = MoviesPagingSource(api, "popular").load(refresh())

        assertTrue(result is LoadResult.Error)
        assertEquals(failure, (result as LoadResult.Error).throwable)
    }

    // --- endpoint selection ---

    @Test
    fun `trending movies use the trending endpoint and its time window`() = runTest {
        coEvery { api.getTrendingMovies("day", 1) } returns MoviesResponse(results = listOf(Movie(id = 1)), totalPages = 1)

        MoviesPagingSource(api, movieType = "day", isTrending = true).load(refresh())

        coVerify(exactly = 1) { api.getTrendingMovies("day", 1) }
        coVerify(exactly = 0) { api.getAllMovies(any(), any()) }
    }

    @Test
    fun `shows page through the category endpoint, or trending when asked`() = runTest {
        val shows = arrayListOf(TvShow(id = 7))
        coEvery { api.getAllTvShows("top_rated", 1) } returns TvShowResponse(results = shows, totalPages = 2)
        coEvery { api.getTrendingTvShows("day", 1) } returns TvShowResponse(results = shows, totalPages = 1)

        val category = TvShowsPagingSource(api, "top_rated").load(refresh()).page()
        val trending = TvShowsPagingSource(api, showType = "day", isTrending = true).load(refresh()).page()

        assertEquals(shows, category.data)
        assertEquals(2, category.nextKey)
        assertNull(trending.nextKey)
        coVerify(exactly = 1) { api.getTrendingTvShows("day", 1) }
    }

    @Test
    fun `people page through the popular people endpoint`() = runTest {
        val people = listOf(Person(id = 9))
        coEvery { api.getPopularPeople(2) } returns PeopleResponse(results = people, totalPages = 4)

        val page = PeoplePagingSource(api).load(refresh(key = 2)).page()

        assertEquals(people, page.data)
        assertEquals(1, page.prevKey)
        assertEquals(3, page.nextKey)
    }

    // --- discover filters ---

    @Test
    fun `discover movies sends the chosen filters, joining genres with OR`() = runTest {
        val filters = DiscoverFilters(
            genreIds = linkedSetOf(28, 12),
            year = 2020,
            minRating = 7f,
            sortBy = DiscoverSort.POPULARITY_DESC,
            keywordId = 42
        )
        coEvery { api.discoverMovies("28|12", 2020, 7f, "42", DiscoverSort.POPULARITY_DESC.apiValue, 1) } returns
            MoviesResponse(results = listOf(Movie(id = 1)), totalPages = 1)

        val result = DiscoverMoviesPagingSource(api, filters).load(refresh())

        assertTrue(result is LoadResult.Page)
        coVerify(exactly = 1) { api.discoverMovies("28|12", 2020, 7f, "42", DiscoverSort.POPULARITY_DESC.apiValue, 1) }
    }

    @Test
    fun `discover shows leaves unset filters out of the request`() = runTest {
        coEvery { api.discoverTv(null, null, null, null, DiscoverSort.POPULARITY_DESC.apiValue, 1) } returns
            TvShowResponse(results = arrayListOf(TvShow(id = 1)), totalPages = 1)

        val result = DiscoverTvPagingSource(api, DiscoverFilters()).load(refresh())

        assertTrue(result is LoadResult.Page)
        coVerify(exactly = 1) { api.discoverTv(null, null, null, null, DiscoverSort.POPULARITY_DESC.apiValue, 1) }
    }
}
