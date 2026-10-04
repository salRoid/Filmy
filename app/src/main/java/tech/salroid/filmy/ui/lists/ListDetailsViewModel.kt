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
import tech.salroid.filmy.data.local.model.account.TmdbListItem
import tech.salroid.filmy.ui.home.AccountRepository
import javax.inject.Inject

// TMDB serves list items 20 to a page; this is a guard against a runaway
// loop, not a limit anyone's personal list should reach.
private const val MAX_LIST_PAGES = 50

@HiltViewModel
class ListDetailsViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<TmdbListItem>>(emptyList())
    val items: StateFlow<List<TmdbListItem>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadListDetails(listId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!accountRepository.canManageLists()) return@launch
            _isLoading.emit(true)
            try {
                val loaded = mutableListOf<TmdbListItem>()
                var page = 1
                do {
                    val response = accountRepository.getListDetails(listId, page).first()
                    loaded += response.items
                    val totalPages = response.totalPages ?: 1
                    page++
                } while (page <= totalPages && page <= MAX_LIST_PAGES)
                _items.emit(loaded)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    /** Optimistically removes the item locally, rolling back if the TMDB removal fails. */
    fun removeItem(listId: Int, item: TmdbListItem) {
        val previous = _items.value
        // A movie and a show can share an id, so match on both.
        _items.value = _items.value.filterNot { it.id == item.id && it.isTv == item.isTv }

        viewModelScope.launch(Dispatchers.IO) {
            val removed = try {
                accountRepository.canManageLists() &&
                    accountRepository.removeFromList(listId, item.id, item.isTv).first().allSucceeded
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
            if (!removed) {
                _items.value = previous
            }
        }
    }
}
