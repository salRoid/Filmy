package tech.salroid.filmy.ui.movies.details.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.common.components.LoginRequiredDialog
import tech.salroid.filmy.ui.details.MovieDetailsViewModel
import tech.salroid.filmy.ui.home.LoginViewModel
import tech.salroid.filmy.ui.home.rememberLoginLauncher

/**
 * Everything that happens after tapping "Add to List" on a movie or show:
 * the list picker when the user can manage lists, otherwise a login prompt
 * that turns into the picker in place as soon as the login completes - no
 * need to leave the details screen and come back.
 *
 * Compose this only while the flow is open; [onDismiss] closes it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToListFlow(
    mediaId: Int,
    isTv: Boolean,
    viewModel: MovieDetailsViewModel,
    loginViewModel: LoginViewModel,
    onDismiss: () -> Unit
) {
    val canManageLists by loginViewModel.canManageLists.collectAsStateWithLifecycle()
    val startLogin = rememberLoginLauncher(loginViewModel)

    if (canManageLists) {
        val userLists by viewModel.userLists.collectAsStateWithLifecycle()
        val listMembership by viewModel.listMembership.collectAsStateWithLifecycle()
        val isLoading by viewModel.userListsLoading.collectAsStateWithLifecycle()

        LaunchedEffect(mediaId, isTv) {
            viewModel.loadUserLists(mediaId, isTv)
        }

        AddToListSheet(
            lists = userLists,
            membership = listMembership,
            isLoading = isLoading,
            onToggle = { listId, currentlyIn ->
                viewModel.toggleListMembership(listId, mediaId, isTv, currentlyIn)
            },
            onCreateList = { name -> viewModel.createList(name, mediaId, isTv) },
            onDismiss = onDismiss
        )
    } else {
        LoginRequiredDialog(
            // Someone with a session but no list access logged in before
            // lists could hold shows; one more login upgrades them.
            message = if (viewModel.isLoggedIn()) {
                stringResource(R.string.lists_relogin_required_message)
            } else {
                stringResource(R.string.login_required_add_to_list_message)
            },
            onLogin = startLogin,
            onDismiss = onDismiss
        )
    }
}
