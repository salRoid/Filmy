package tech.salroid.filmy.ui.season

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
import tech.salroid.filmy.data.local.model.tv.SeasonDetailsResponse
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _season = MutableStateFlow<SeasonDetailsResponse?>(null)
    val season: StateFlow<SeasonDetailsResponse?> = _season.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadSeason(tvId: String, seasonNumber: Int) {
        viewModelScope.launch {
            _isLoading.emit(true)
            moviesRepository.getSeasonDetails(tvId, seasonNumber)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _season.emit(it) }
            _isLoading.emit(false)
        }
    }
}
