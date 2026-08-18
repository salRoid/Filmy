package tech.salroid.filmy.ui.franchise

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
import tech.salroid.filmy.data.local.model.collection.CollectionDetailsResponse
import tech.salroid.filmy.ui.home.MoviesRepository
import javax.inject.Inject

@HiltViewModel
class FranchiseViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    private val _collection = MutableStateFlow<CollectionDetailsResponse?>(null)
    val collection: StateFlow<CollectionDetailsResponse?> = _collection.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadCollection(collectionId: Int) {
        viewModelScope.launch {
            _isLoading.emit(true)
            moviesRepository.getCollectionDetails(collectionId)
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { _collection.emit(it) }
            _isLoading.emit(false)
        }
    }
}
