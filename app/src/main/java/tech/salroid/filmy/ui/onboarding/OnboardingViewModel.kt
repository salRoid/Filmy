package tech.salroid.filmy.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.utility.ImageConfig
import tech.salroid.filmy.utility.IpGeolocation
import javax.inject.Inject

/**
 * Purely decorative - powers the animated poster backdrop on the onboarding
 * screen. Best-effort only: a failed fetch just leaves the backdrop empty,
 * it never blocks or affects the actual country-selection flow.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _posterUrls = MutableStateFlow<List<String>>(emptyList())
    val posterUrls: StateFlow<List<String>> = _posterUrls.asStateFlow()

    /** Best-effort IP-based country guess, null until resolved (or if it fails). */
    private val _detectedCountry = MutableStateFlow<String?>(null)
    val detectedCountry: StateFlow<String?> = _detectedCountry.asStateFlow()

    init {
        viewModelScope.launch {
            moviesRepository.getMoviesFlow(type = "day", isTrending = true)
                .collect { result ->
                    result.getOrNull()?.results?.let { movies ->
                        _posterUrls.value = movies.mapNotNull { it.posterPath }
                            .map { ImageConfig.BASE_URL + it }
                            .shuffled()
                    }
                }
        }
        viewModelScope.launch {
            _detectedCountry.value = IpGeolocation.fetchCountryCode()
        }
    }
}
