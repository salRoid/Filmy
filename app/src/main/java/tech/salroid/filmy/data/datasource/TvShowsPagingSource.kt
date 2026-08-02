package tech.salroid.filmy.data.datasource

import  androidx.paging.PagingSource
import androidx.paging.PagingState
import tech.salroid.filmy.data.local.model.TvShow
import tech.salroid.filmy.data.network.MoviesApiService

class TvShowsPagingSource(
    private val apiService: MoviesApiService,
    private val showType: String,
    private val isTrending: Boolean = false,
    private val trendingTimeWindow: String = "day"
) : PagingSource<Int, TvShow>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TvShow> {
        return try {
            val currentLoadingPageKey = params.key ?: 1
            val response = when (isTrending) {
                true -> apiService.getTrendingTvShows(timeWindow = trendingTimeWindow, currentLoadingPageKey)
                else -> apiService.getAllTvShows(showType, currentLoadingPageKey)
            }

            val results = response.results

            val prevKey =
                if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

            LoadResult.Page(
                data = results,
                prevKey = prevKey,
                nextKey = currentLoadingPageKey.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * The refresh key is used for subsequent calls to PagingSource.Load after the initial load.
     */
    override fun getRefreshKey(state: PagingState<Int, TvShow>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index.
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}