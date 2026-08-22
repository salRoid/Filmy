package tech.salroid.filmy.ui.gallery

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import tech.salroid.filmy.MainDispatcherRule
import tech.salroid.filmy.data.local.model.ImagesResponse
import tech.salroid.filmy.ui.home.MoviesRepository

class GalleryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var moviesRepository: MoviesRepository
    private lateinit var viewModel: GalleryViewModel

    @Before
    fun setUp() {
        moviesRepository = mockk()
        viewModel = GalleryViewModel(moviesRepository)
    }

    @After
    fun tearDown() {
        viewModel.viewModelScope.cancel()
    }

    @Test
    fun `loadImages fetches movie images when isTv is false`() = runTest {
        val response = ImagesResponse()
        every { moviesRepository.getMovieImages("5") } returns flowOf(response)

        viewModel.images.test {
            assertNull(awaitItem())
            viewModel.loadImages(5, isTv = false)
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(exactly = 0) { moviesRepository.getTvImages(any()) }
    }

    @Test
    fun `loadImages fetches tv images when isTv is true`() = runTest {
        val response = ImagesResponse()
        every { moviesRepository.getTvImages("5") } returns flowOf(response)

        viewModel.images.test {
            assertNull(awaitItem())
            viewModel.loadImages(5, isTv = true)
            assertEquals(response, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        verify(exactly = 0) { moviesRepository.getMovieImages(any()) }
    }
}
