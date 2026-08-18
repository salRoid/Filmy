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
import tech.salroid.filmy.data.local.model.account.TmdbList
import tech.salroid.filmy.ui.home.AccountRepository
import javax.inject.Inject

@HiltViewModel
class MyListsViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _lists = MutableStateFlow<List<TmdbList>>(emptyList())
    val lists: StateFlow<List<TmdbList>> = _lists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadLists()
    }

    fun loadLists() {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = accountRepository.getSessionIdFromPref() ?: return@launch
            val accountId = accountRepository.getProfileFromLocal()?.id ?: return@launch
            _isLoading.emit(true)
            try {
                val response = accountRepository.getLists(accountId, sessionId).first()
                _lists.emit(response.results)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun createList(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = accountRepository.getSessionIdFromPref() ?: return@launch
            try {
                val response = accountRepository.createList(sessionId, name).first()
                val listId = response.listId ?: return@launch
                _lists.value = _lists.value + TmdbList(id = listId, name = name, itemCount = 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Optimistically removes the list locally, rolling back if the TMDB delete fails. */
    fun deleteList(listId: Int) {
        val previous = _lists.value
        _lists.value = _lists.value.filterNot { it.id == listId }

        viewModelScope.launch(Dispatchers.IO) {
            val sessionId = accountRepository.getSessionIdFromPref()
            if (sessionId == null) {
                _lists.value = previous
                return@launch
            }
            try {
                accountRepository.deleteList(listId, sessionId).first()
            } catch (e: Exception) {
                e.printStackTrace()
                _lists.value = previous
            }
        }
    }
}
