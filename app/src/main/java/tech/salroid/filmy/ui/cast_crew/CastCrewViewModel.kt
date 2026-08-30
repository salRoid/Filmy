package tech.salroid.filmy.ui.cast_crew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.model.CastAndCrewResponse
import tech.salroid.filmy.data.local.model.CastCrewDetailsResponse
import tech.salroid.filmy.data.local.model.CastCrewMoviesResponse
import tech.salroid.filmy.data.local.model.CombinedCreditsResponse
import tech.salroid.filmy.data.local.model.ExternalIdsResponse
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class CastCrewViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _uiStateCastAndCrew = MutableStateFlow<CastAndCrewResponse?>(null)
    private val _uiStateCastCrewDetails = MutableStateFlow<CastCrewDetailsResponse?>(null)
    private val _uiStateCastCrewMovies = MutableStateFlow<CastCrewMoviesResponse?>(null)
    private val _uiStateCombinedCredits = MutableStateFlow<CombinedCreditsResponse?>(null)
    private val _uiStateExternalIds = MutableStateFlow<ExternalIdsResponse?>(null)
    private val _uiStateError = MutableStateFlow(false)
    val uiStateCastAndCrew: StateFlow<CastAndCrewResponse?> = _uiStateCastAndCrew.asStateFlow()
    val uiStateCastCrewDetails: StateFlow<CastCrewDetailsResponse?> =
        _uiStateCastCrewDetails.asStateFlow()
    val uiStateCastCrewMovies: StateFlow<CastCrewMoviesResponse?> =
        _uiStateCastCrewMovies.asStateFlow()
    val uiStateCombinedCredits: StateFlow<CombinedCreditsResponse?> =
        _uiStateCombinedCredits.asStateFlow()
    val uiStateExternalIds: StateFlow<ExternalIdsResponse?> = _uiStateExternalIds.asStateFlow()
    /** True when the core person details fetch failed - there's no local cache to fall back on. */
    val uiStateError: StateFlow<Boolean> = _uiStateError.asStateFlow()

    fun getCastAndCrew(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getCastAndCrew(movieId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { castAndCrew ->
                    _uiStateCastAndCrew.emit(castAndCrew)
                }
        }
    }

    fun getCastAndCrewTv(tvId: String) {
        viewModelScope.launch {
            moviesRepository.getCastAndCrewTv(tvId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { castAndCrew ->
                    _uiStateCastAndCrew.emit(castAndCrew)
                }
        }
    }

    fun getCastCrewDetails(memberId: String) {
        _uiStateError.value = false
        viewModelScope.launch {
            moviesRepository.getCastCrewDetails(memberId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                    _uiStateError.emit(true)
                }.collect { castCrewDetails ->
                    _uiStateCastCrewDetails.emit(castCrewDetails)
                }
        }
    }

    fun getCastCrewMovies(memberId: String) {
        viewModelScope.launch {
            moviesRepository.getCastCrewMovies(memberId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { castCrewMovies ->
                    _uiStateCastCrewMovies.emit(castCrewMovies)
                }
        }
    }

    fun getCastCrewTvShows(memberId: String) {
        viewModelScope.launch {
            moviesRepository.getCastCrewTvShows(memberId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { castCrewMovies ->
                    _uiStateCastCrewMovies.emit(castCrewMovies)
                }
        }
    }

    fun getCombinedCredits(memberId: String) {
        viewModelScope.launch {
            moviesRepository.getCombinedCredits(memberId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { combinedCredits ->
                    _uiStateCombinedCredits.emit(combinedCredits)
                }
        }
    }

    fun getPersonExternalIds(memberId: String) {
        viewModelScope.launch {
            moviesRepository.getPersonExternalIds(memberId)
                .flowOn(Dispatchers.IO)
                .catch {
                    it.printStackTrace()
                }.collect { externalIds ->
                    _uiStateExternalIds.emit(externalIds)
                }
        }
    }
}
