package tech.salroid.filmy.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.model.ImagesResponse
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _images = MutableStateFlow<ImagesResponse?>(null)
    val images: StateFlow<ImagesResponse?> = _images.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadImages(id: Int, isTv: Boolean) {
        viewModelScope.launch {
            _isLoading.emit(true)
            val flow = if (isTv) moviesRepository.getTvImages(id.toString()) else moviesRepository.getMovieImages(id.toString())
            flow.flowOn(Dispatchers.IO)
                .catch { }
                .collect { _images.emit(it) }
            _isLoading.emit(false)
        }
    }
}
