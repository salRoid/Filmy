package tech.salroid.filmy.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.salroid.filmy.data.local.model.account.TmdbList
import tech.salroid.filmy.ui.home.AccountRepository
import javax.inject.Inject
import tech.salroid.filmy.utility.reportNonFatal

/** How many titles a list card shows before "+N more". */
internal const val PREVIEW_TITLE_COUNT = 4

@HiltViewModel
class MyListsViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _lists = MutableStateFlow<List<TmdbList>>(emptyList())
    val lists: StateFlow<List<TmdbList>> = _lists.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * The first few titles in each list, keyed by list id, for the list
     * cards. A list is absent until its titles have loaded; the cards don't
     * wait for them.
     */
    private val _itemPreviews = MutableStateFlow<Map<Int, List<String>>>(emptyMap())
    val itemPreviews: StateFlow<Map<Int, List<String>>> = _itemPreviews.asStateFlow()

    init {
        loadLists()
    }

    fun loadLists() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!accountRepository.canManageLists()) return@launch
            _isLoading.emit(true)
            try {
                val response = accountRepository.getLists().first()
                _lists.emit(response.results)
                _isLoading.emit(false)
                loadItemPreviews(response.results)
            } catch (e: Exception) {
                e.reportNonFatal()
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    /**
     * TMDB's list overview carries no items, so each non-empty list's first
     * page is fetched for its leading titles. One list failing only leaves
     * that card without titles.
     */
    private suspend fun loadItemPreviews(lists: List<TmdbList>) = coroutineScope {
        lists.filter { (it.itemCount ?: 0) > 0 }.forEach { list ->
            launch {
                try {
                    val titles = accountRepository.getListDetails(list.id).first().items
                        .map { it.displayTitle }
                        .filter { it.isNotBlank() }
                        .take(PREVIEW_TITLE_COUNT)
                    _itemPreviews.update { it + (list.id to titles) }
                } catch (e: Exception) {
                    e.reportNonFatal()
                }
            }
        }
    }

    fun createList(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!accountRepository.canManageLists()) return@launch
            try {
                val response = accountRepository.createList(name).first()
                val listId = response.listId ?: return@launch
                _lists.value = _lists.value + TmdbList(id = listId, name = name, itemCount = 0)
            } catch (e: Exception) {
                e.reportNonFatal()
            }
        }
    }

    /** Optimistically removes the list locally, rolling back if the TMDB delete fails. */
    fun deleteList(listId: Int) {
        val previous = _lists.value
        val previousPreviews = _itemPreviews.value
        _lists.value = _lists.value.filterNot { it.id == listId }
        _itemPreviews.update { it - listId }

        viewModelScope.launch(Dispatchers.IO) {
            if (!accountRepository.canManageLists()) {
                _lists.value = previous
                _itemPreviews.value = previousPreviews
                return@launch
            }
            try {
                accountRepository.deleteList(listId).first()
            } catch (e: Exception) {
                e.reportNonFatal()
                _lists.value = previous
                _itemPreviews.value = previousPreviews
            }
        }
    }
}
