package tech.salroid.filmy.ui.similar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.model.SimilarMoviesResponse
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class SimilarViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiStateSimilar = MutableStateFlow<SimilarMoviesResponse?>(null)
    val uiStateSimilar: StateFlow<SimilarMoviesResponse?> = _uiStateSimilar.asStateFlow()

    fun getSimilar(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getSimilar(movieId)
                .flowOn(Dispatchers.IO)
                .catch {
                    // error
                }.collect { similarResponse ->
                    _uiStateSimilar.emit(similarResponse)
                }
        }
    }
}