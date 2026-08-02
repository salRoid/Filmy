package tech.salroid.filmy.ui.similar_recommendation

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
class SimilarRecommendationViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiStateSimilar = MutableStateFlow<SimilarMoviesResponse?>(null)
    private val _uiStateRecommendation = MutableStateFlow<SimilarMoviesResponse?>(null)
    val uiStateSimilar: StateFlow<SimilarMoviesResponse?> = _uiStateSimilar.asStateFlow()
    val uiStateRecommendation: StateFlow<SimilarMoviesResponse?> = _uiStateRecommendation.asStateFlow()

    fun getSimilar(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getSimilar(movieId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { similarResponse ->
                    _uiStateSimilar.emit(similarResponse)
                }
        }
    }

    fun getSimilarTv(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getSimilarTv(movieId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { similarResponse ->
                    _uiStateSimilar.emit(similarResponse)
                }
        }
    }

    fun getRecommendation(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getRecommendation(movieId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { similarResponse ->
                    _uiStateRecommendation.emit(similarResponse)
                }
        }
    }

    fun getRecommendationTv(tvId: String) {
        viewModelScope.launch {
            moviesRepository.getRecommendationTv(tvId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { similarResponse ->
                    _uiStateRecommendation.emit(similarResponse)
                }
        }
    }
}