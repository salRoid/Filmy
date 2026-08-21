package tech.salroid.filmy.ui.search

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.utility.PreferenceHelper.addRecentSearch
import tech.salroid.filmy.utility.PreferenceHelper.clearRecentSearches
import tech.salroid.filmy.utility.PreferenceHelper.recentSearches
import tech.salroid.filmy.utility.PreferenceHelper.removeRecentSearch
import javax.inject.Inject

@HiltViewModel
@OptIn(
    FlowPreview::class,
    ExperimentalCoroutinesApi::class
)
class SearchViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val searchPreviewMapper: SearchPreviewMapper,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(sharedPreferences.recentSearches())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

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

    fun commitSearch(query: String) {
        if (query.isBlank()) return
        sharedPreferences.addRecentSearch(query)
        _recentSearches.value = sharedPreferences.recentSearches()
    }

    fun removeRecentSearch(query: String) {
        sharedPreferences.removeRecentSearch(query)
        _recentSearches.value = sharedPreferences.recentSearches()
    }

    fun clearRecentSearches() {
        sharedPreferences.clearRecentSearches()
        _recentSearches.value = emptyList()
    }
}
