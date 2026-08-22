package tech.salroid.filmy.ui.common.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import tech.salroid.filmy.R

@Composable
fun LoginRequiredDialog(
    message: String,
    onLogin: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(R.string.login_required_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onLogin) {
                Text(stringResource(R.string.login_now))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}
