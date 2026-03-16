package tech.salroid.filmy.ui.shows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class ShowsViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val mapper: TvShowsPreviewMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShowsScreenState>(
        ShowsScreenState.Loading
    )
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        load()
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = ShowsScreenState.Loading
            moviesRepository.getTvShowsFlow(
                type = "trending",
                isTrending = true
            ).collect { result ->
                val state = result.fold(onSuccess = { response ->
                    runCatching {
                        response.results
                            .map(mapper::map)
                    }.fold(
                        onSuccess = { ShowsScreenState.Success(it) },
                        onFailure = { ShowsScreenState.Error("Mapping Error!") }
                    )
                }, onFailure = { error ->
                    ShowsScreenState.Error(
                        error.message ?: "Something went wrong!"
                    )
                })
                _uiState.value = state as ShowsScreenState
            }
        }
    }

    fun retry() {
        load()
    }
}