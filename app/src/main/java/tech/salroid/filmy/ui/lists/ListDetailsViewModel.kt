package tech.salroid.filmy.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.db.entity.Movie
import tech.salroid.filmy.ui.home.AccountRepository
import javax.inject.Inject

@HiltViewModel
class ListDetailsViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<Movie>>(emptyList())
    val items: StateFlow<List<Movie>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadListDetails(listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = accountRepository.getSessionIdFromPref() ?: return@launch
            _isLoading.emit(true)
            try {
                val response = accountRepository.getListDetails(listId, sessionId).first()
                _items.emit(response.items)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    /** Optimistically removes the item locally, rolling back if the TMDB removal fails. */
    fun removeItem(listId: Int, movie: Movie) {
        val previous = _items.value
        _items.value = _items.value.filterNot { it.id == movie.id }

        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = accountRepository.getSessionIdFromPref()
            if (sessionId == null) {
                _items.value = previous
                return@launch
            }
            try {
                accountRepository.removeFromList(listId, sessionId, movie.id).first()
            } catch (e: Exception) {
                e.printStackTrace()
                _items.value = previous
            }
        }
    }
}
