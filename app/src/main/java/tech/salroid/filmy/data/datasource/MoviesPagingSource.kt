package tech.salroid.filmy.data.datasource

import  androidx.paging.PagingSource
import androidx.paging.PagingState
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.network.MoviesApiService

class MoviesPagingSource(
    private val apiService: MoviesApiService,
    private val movieType: String,
    private val isTrending: Boolean = false,
    private val trendingTimeWindow: String = "day"
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val currentLoadingPageKey = params.key ?: 1
            val response = when (isTrending) {
                true -> apiService.getTrendingMovies(timeWindow = trendingTimeWindow, currentLoadingPageKey)
                else -> apiService.getAllMovies(movieType, currentLoadingPageKey)
            }

            val results = response.results
            val totalPages = response.totalPages ?: 0

            val prevKey =
                if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

            val nextKey =
                if (currentLoadingPageKey >= totalPages || results.isEmpty()) null else currentLoadingPageKey + 1

            LoadResult.Page(
                data = results,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * The refresh key is used for subsequent calls to PagingSource.Load after the initial load.
     */
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index.
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}