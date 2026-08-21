package tech.salroid.filmy.ui.home

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Drives the TMDB v4 Custom-Tabs OAuth flow (request token -> Custom Tab ->
 * resume -> access token -> session -> profile) against [viewModel], usable
 * from any screen - not just the Account screen. Returns a callback that
 * kicks the flow off; the rest happens via this composable's own effects, so
 * the caller just needs to observe [LoginViewModel.uiStateProfile] to know
 * when login completes.
 */
@Composable
fun rememberLoginLauncher(viewModel: LoginViewModel): () -> Unit {
    val customTabLauncher = rememberLauncherForActivityResult(
        contract = object : ActivityResultContract<String, Int>() {
            override fun createIntent(context: Context, input: String): Intent {
                val customTabsIntent = CustomTabsIntent.Builder().build().intent
                customTabsIntent.data = input.toUri()
                return customTabsIntent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Int = resultCode
        }
    ) { }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (viewModel.requestToken != null && viewModel.sessionId == null) {
                    viewModel.getAccessToken()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val uiStateToken by viewModel.uiStateToken.collectAsState()
    LaunchedEffect(uiStateToken) {
        uiStateToken?.requestToken?.let { token ->
            if (viewModel.accessToken == null) {
                val url = "https://www.themoviedb.org/auth/access?request_token=$token"
                customTabLauncher.launch(url)
            }
        }
    }

    return { viewModel.getRequestToken() }
}
