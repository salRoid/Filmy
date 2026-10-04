/*
package tech.salroid.filmy.ui.textvalidation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TextProcessingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.NotPalindrome)
    var uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private var textProcessingJob: Job? = null

    fun onTextInput(text: String) {
        textProcessingJob?.cancel()
        textProcessingJob = viewModelScope.launch {
            val result = checkPalindrome(text)
            if (result) {
                _uiState.value = UiState.Palindrome
            } else {
                _uiState.value = UiState.NotPalindrome
            }
        }
    }

    private fun checkPalindrome(text: String): Boolean {
        var start = 0
        var end = text.length - 1

        while (start < end) {
            if (text[start] !in 'A'..'Z' &&
                text[start] !in 'a'..'z' &&
                text[start] !in '0'..'9'
            ) {
                start++
                continue
            }

            if (text[end] !in 'A'..'Z' &&
                text[end] !in 'a'..'z' &&
                text[end] !in '0'..'9'
            ) {
                end--
                continue
            }

            if (text[start] == text[end]) {
                start++
                end--
            } else {
                break
            }
        }
        return start >= end
    }
}


sealed class UiState {
    object Palindrome : UiState()
    object NotPalindrome : UiState()
}*/
