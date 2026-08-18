package tech.salroid.filmy.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.data.local.model.discover.DiscoverFilters
import tech.salroid.filmy.data.network.MoviesApiService

class DiscoverMoviesPagingSource(
    private val apiService: MoviesApiService,
    private val filters: DiscoverFilters
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val currentLoadingPageKey = params.key ?: 1
            val response = apiService.discoverMovies(
                withGenres = filters.genreIds.takeIf { it.isNotEmpty() }?.joinToString("|"),
                year = filters.year,
                minRating = filters.minRating,
                withKeywords = filters.keywordId?.toString(),
                sortBy = filters.sortBy.apiValue,
                page = currentLoadingPageKey
            )

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

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
