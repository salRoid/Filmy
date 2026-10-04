package tech.salroid.filmy.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import tech.salroid.filmy.data.local.model.Person
import tech.salroid.filmy.data.network.MoviesApiService

class PeoplePagingSource(
    private val apiService: MoviesApiService
) : PagingSource<Int, Person>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Person> {
        return try {
            val currentLoadingPageKey = params.key ?: 1
            val response = apiService.getPopularPeople(currentLoadingPageKey)

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

    override fun getRefreshKey(state: PagingState<Int, Person>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
