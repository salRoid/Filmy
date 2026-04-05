package tech.salroid.filmy.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
@OptIn(
    FlowPreview::class,
    ExperimentalCoroutinesApi::class
)
class SearchViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val searchPreviewMapper: SearchPreviewMapper
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val uiState = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(SearchScreenState.Idle)
            } else {
                flow<SearchScreenState> {
                    emit(SearchScreenState.Loading)
                    
                    moviesRepository
                        .searchMultiFlow(query)
                        .collect { searchResultResponse ->
                            searchResultResponse.fold(onSuccess = { response ->
                                runCatching {
                                    response.results
                                        .map(searchPreviewMapper::map)
                                }.fold(onSuccess = { previews ->
                                    emit(SearchScreenState.Success(previews))
                                }, onFailure = { exception ->
                                    emit(SearchScreenState.Error(exception.message ?: "Mapping Error"))
                                })
                            }, onFailure = { exception ->
                                emit(SearchScreenState.Error(exception.message ?: "Something went wrong."))
                            })
                        }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchScreenState.Idle
        )

    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
        }
    }
}
