/*
package tech.salroid.filmy.ui.textvalidation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


// 1. UI Component -> Compose
// 2. Sending Text Stream -> VM
// 3. Processing Text Stream -> VM
// 4. UI State -> VM
// 4. Subscribe UI State -> UI Component
@Composable
fun TextProcessingScreen(
    modifier: Modifier = Modifier,
    viewModel: TextProcessingViewModel = viewModel(),
) {

    val textFieldState = remember { TextFieldState() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val text = when (uiState) {
        UiState.NotPalindrome -> "Not Palindrome"
        UiState.Palindrome -> "Palindrome"
    }

    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .collect {
                viewModel.onTextInput(it)
            }
    }

    Column(modifier = modifier) {
        TextField(
            state = textFieldState,
            placeholder = { Text("Enter the text") },
        )

        Text(text = text)
    }
}

@Preview
@Composable
fun TextScreenPreview() {
    TextProcessingScreen(
        modifier = Modifier.fillMaxSize()
    )
}*/
