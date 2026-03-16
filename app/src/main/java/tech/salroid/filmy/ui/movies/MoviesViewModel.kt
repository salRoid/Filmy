package tech.salroid.filmy.ui.movies

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
class MoviesViewModel @Inject constructor(
    val moviesRepository: MoviesRepository,
    val moviePreviewMapper: MoviePreviewMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<MoviesScreenState>(
        MoviesScreenState.Loading
    )
    val uiState = _uiState.asStateFlow()
    private var loadJob: Job? = null

    init {
        load()
    }

    private fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = MoviesScreenState.Loading
            moviesRepository.getMoviesFlow(
                type = "trending",
                isTrending = true
            ).collect { result ->
                val state = result.fold(onSuccess = { response ->
                    runCatching {
                        response.results
                            .map(moviePreviewMapper::map)
                    }.fold(
                        onSuccess = { MoviesScreenState.Success(it) },
                        onFailure = { MoviesScreenState.Error("Mapping error") }
                    )

                }, onFailure = { error ->
                    MoviesScreenState.Error(
                        error.message ?: "Something went wrong"
                    )
                })
                _uiState.value = state
            }
        }
    }

    fun retry() {
        load()
    }


    /*
      *** Use this approach when we don't want cached UI state in VM ***

     private val _retryTrigger = MutableSharedFlow<Unit>(
         0,
         1
     )

    val movieScreenState: StateFlow<MoviesScreenState> =
        _retryTrigger.onStart {
            emit(Unit)
        }.flatMapLatest {
            moviesRepository.getMoviesFlow(
                type = "trending",
                isTrending = true
            ).map { result ->
                result.fold(onSuccess = { response ->
                    runCatching {
                        response.results
                            .map(moviePreviewMapper::map)
                    }.fold(
                        onSuccess = { MoviesScreenState.Success(it) },
                        onFailure = { MoviesScreenState.Error("Mapping error") })
                }, onFailure = { error ->
                    MoviesScreenState.Error(
                        error.message ?: "Something went wrong"
                    )
                })
            }.onStart {
                emit(MoviesScreenState.Loading)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MoviesScreenState.Loading
        )

    fun retry() {
        _retryTrigger.tryEmit(Unit)
    }*/
}
