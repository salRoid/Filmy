package tech.salroid.filmy.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen() {
    Scaffold { paddingValues ->
        MoviesScreen(
            modifier = Modifier.padding(paddingValues)
        )
    }
}