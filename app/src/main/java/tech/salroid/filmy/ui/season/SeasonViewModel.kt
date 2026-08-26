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
import tech.salroid.filmy.ui.common.model.WatchProvidersUiModel
import tech.salroid.filmy.ui.details.MediaDetailsMapper
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class SeasonViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val mediaDetailsMapper: MediaDetailsMapper
) : ViewModel() {

    private val _season = MutableStateFlow<SeasonDetailsResponse?>(null)
    val season: StateFlow<SeasonDetailsResponse?> = _season.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // episode number -> "X.X/10". Best-effort: an empty map just means no
    // badges show, it never blocks or delays the episode list itself.
    private val _episodeRatings = MutableStateFlow<Map<Int, String>>(emptyMap())
    val episodeRatings: StateFlow<Map<Int, String>> = _episodeRatings.asStateFlow()

    // TMDB has no per-episode watch-provider data, so this is the show's
    // own providers (same source as the details screen), surfaced when an
    // episode is tapped. Best-effort, same as episodeRatings above.
    private val _watchProviders = MutableStateFlow<WatchProvidersUiModel?>(null)
    val watchProviders: StateFlow<WatchProvidersUiModel?> = _watchProviders.asStateFlow()

    fun loadSeason(tvId: String, seasonNumber: Int) {
        viewModelScope.launch {
            _isLoading.emit(true)
            moviesRepository.getSeasonDetails(tvId, seasonNumber)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _season.emit(it) }
            _isLoading.emit(false)
        }
        loadEpisodeRatings(tvId, seasonNumber)
        loadWatchProviders(tvId)
    }

    private fun loadWatchProviders(tvId: String) {
        viewModelScope.launch {
            moviesRepository.getWatchProvidersTv(tvId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { response ->
                    _watchProviders.emit(mediaDetailsMapper.mapWatchProviders(response))
                }
        }
    }

    private fun loadEpisodeRatings(tvId: String, seasonNumber: Int) {
        viewModelScope.launch {
            moviesRepository.getTvExternalIds(tvId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { externalIds ->
                    val imdbId = externalIds.imdbId ?: return@collect
                    moviesRepository.getSeasonRatings(imdbId, seasonNumber)
                        .flowOn(Dispatchers.IO)
                        .catch { }
                        .collect { response ->
                            _episodeRatings.emit(
                                response.episodes
                                    .filter { !it.imdbRating.isNullOrEmpty() && it.imdbRating != "N/A" }
                                    .mapNotNull { rating -> rating.episode?.let { it to "${rating.imdbRating}/10" } }
                                    .toMap()
                            )
                        }
                }
        }
    }
}
