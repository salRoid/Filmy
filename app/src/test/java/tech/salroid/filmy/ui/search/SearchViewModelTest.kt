package tech.salroid.filmy.ui.search

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
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
import tech.salroid.filmy.data.local.model.SearchResult
import tech.salroid.filmy.data.local.model.SearchResultResponse
import tech.salroid.filmy.ui.home.MoviesRepository

class SearchViewModelTest {

    // uiState's debounce(300) needs virtual time control shared with
    // Dispatchers.Main - a MainDispatcherRule with its own disconnected
    // scheduler wouldn't let runTest's advanceTimeBy affect it, so this test
    // explicitly passes the rule's dispatcher into runTest.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var sharedPreferences: FakeSharedPreferences
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        moviesRepository = mockk()
        sharedPreferences = FakeSharedPreferences()
        viewModel = SearchViewModel(moviesRepository, SearchPreviewMapper(), sharedPreferences)
    }

    @After
    fun tearDown() {
        viewModel.cancelScopeAndJoin()
    }

    @Test
    fun `uiState is Idle before any query is entered`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.uiState.test {
            assertEquals(SearchScreenState.Idle, awaitItem())
        }
    }

    @Test
    fun `uiState is Idle for a blank query, without calling the repository`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.uiState.test {
            assertEquals(SearchScreenState.Idle, awaitItem())
            viewModel.onSearchQueryChange("   ")
            advanceTimeBy(301)
            expectNoEvents()
        }
    }

    @Test
    fun `uiState debounces and then settles on Success`() = runTest(mainDispatcherRule.testDispatcher) {
        val result = SearchResult(id = 1, title = "A Movie")
        every { moviesRepository.searchMultiFlow("batman") } returns
            flowOf(Result.success(SearchResultResponse(results = arrayListOf(result))))

        viewModel.uiState.test {
            assertEquals(SearchScreenState.Idle, awaitItem())

            viewModel.onSearchQueryChange("batman")
            advanceTimeBy(299)
            expectNoEvents()

            // Loading is a genuine intermediate state but StateFlow conflates -
            // with the mocked search resolving synchronously, Loading can be
            // overwritten by Success before this collector observes it, so
            // only the settled final state is asserted here.
            advanceTimeBy(2)
            val success = expectMostRecentItem()
            assertTrue(success is SearchScreenState.Success)
            assertEquals(1, (success as SearchScreenState.Success).previews.size)
            assertEquals(1, success.previews.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState surfaces a mapped error message when the search fails`() = runTest(mainDispatcherRule.testDispatcher) {
        val exception = RuntimeException("network down")
        every { moviesRepository.searchMultiFlow("x") } returns flowOf(Result.failure(exception))

        viewModel.uiState.test {
            assertEquals(SearchScreenState.Idle, awaitItem())
            viewModel.onSearchQueryChange("x")
            advanceTimeBy(301)

            val error = expectMostRecentItem()
            assertTrue(error is SearchScreenState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `commitSearch adds to and reads back recent searches`() {
        viewModel.commitSearch("dune")

        assertEquals(listOf("dune"), viewModel.recentSearches.value)
    }

    @Test
    fun `commitSearch ignores a blank query`() {
        viewModel.commitSearch("   ")

        assertEquals(emptyList<String>(), viewModel.recentSearches.value)
    }

    @Test
    fun `removeRecentSearch drops just that entry`() {
        viewModel.commitSearch("dune")
        viewModel.commitSearch("batman")

        viewModel.removeRecentSearch("dune")

        assertEquals(listOf("batman"), viewModel.recentSearches.value)
    }

    @Test
    fun `clearRecentSearches empties the list`() {
        viewModel.commitSearch("dune")

        viewModel.clearRecentSearches()

        assertEquals(emptyList<String>(), viewModel.recentSearches.value)
    }
}
