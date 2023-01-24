package tech.salroid.filmy.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.model.SearchResult
import tech.salroid.filmy.ui.home.MoviesRepository
import tech.salroid.filmy.ui.home.SearchViewUiState
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    val isSearchOpen = MutableStateFlow(false)
    val query = MutableStateFlow("")

    private val _uiStateCloseSearchView = MutableStateFlow(false)
    private val _uiStateSearchView = MutableStateFlow<SearchViewUiState>(SearchViewUiState.Hidden)
    private val _uiStateSearchResult = MutableStateFlow<ArrayList<SearchResult>?>(null)
    val uiStateSearchResults: StateFlow<ArrayList<SearchResult>?> =
        _uiStateSearchResult.asStateFlow()
    val uiStateSearchView: StateFlow<SearchViewUiState?> = _uiStateSearchView.asStateFlow()
    val uiStateCloseSearch: StateFlow<Boolean> = _uiStateCloseSearchView.asStateFlow()

    init {
        searchMovies()
    }

    private fun searchMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            query.debounce(300)
                .filter { query ->
                    return@filter query.isNotEmpty()
                }.distinctUntilChanged().flatMapLatest { query ->
                    moviesRepository.searchMovies(query)
                }.flowOn(Dispatchers.Main).collect { result ->
                    viewModelScope.launch(Dispatchers.Main) {
                        result.results
                            .let {
                                _uiStateSearchResult.emit(it)
                            }
                    }
                }
        }
    }

    fun searchViewVisible(){
        viewModelScope.launch {
            _uiStateSearchView.emit(SearchViewUiState.Visible)
        }
    }

    fun searchViewHidden(){
        viewModelScope.launch {
            _uiStateSearchView.emit(SearchViewUiState.Hidden)
            isSearchOpen.emit(false)
        }
    }

    fun closeSearch() {
        viewModelScope.launch {
            _uiStateCloseSearchView.emit(true)
        }
    }

    fun closeSearchDone() {
        viewModelScope.launch {
            _uiStateCloseSearchView.emit(false)
        }
    }
}