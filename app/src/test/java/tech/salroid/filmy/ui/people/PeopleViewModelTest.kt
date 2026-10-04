package tech.salroid.filmy.ui.people

import androidx.paging.PagingData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.cancelScopeAndJoin
import tech.salroid.filmy.ui.home.MoviesRepository

class PeopleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private var createdViewModel: PeopleViewModel? = null

    @After
    fun tearDown() {
        createdViewModel?.cancelScopeAndJoin()
    }

    @Test
    fun `people fetches from getPeople`() = runTest(mainDispatcherRule.testDispatcher) {
        val moviesRepository: MoviesRepository = mockk()
        every { moviesRepository.getPeople() } returns flowOf(PagingData.empty())

        val viewModel = PeopleViewModel(moviesRepository).also { createdViewModel = it }
        viewModel.people.onEach { }.launchIn(backgroundScope)

        verify(timeout = 1000) { moviesRepository.getPeople() }
    }
}
