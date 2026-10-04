package tech.salroid.filmy

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Swaps Dispatchers.Main for a TestDispatcher so viewModelScope-launched
 * coroutines run under the test. Uses UnconfinedTestDispatcher (not
 * StandardTestDispatcher) deliberately: it has its own scheduler, separate
 * from runTest's internal one, so a StandardTestDispatcher here would
 * silently never get drained by runTest's advanceUntilIdle() - coroutines
 * launched via viewModelScope would just never run.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
