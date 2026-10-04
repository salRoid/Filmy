package tech.salroid.filmy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Cancels the ViewModel's coroutines and waits for them to finish, so none is
 * still winding down on a real thread when MainDispatcherRule resets Main.
 */
fun ViewModel.cancelScopeAndJoin() {
    runBlocking {
        withTimeoutOrNull(5_000) { viewModelScope.coroutineContext.job.cancelAndJoin() }
    }
}
