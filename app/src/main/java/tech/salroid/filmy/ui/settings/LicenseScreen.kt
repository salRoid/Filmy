package tech.salroid.filmy.ui.settings

import android.content.Intent
import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.theme.AppTheme
import tech.salroid.filmy.utility.OPEN_SOURCE_LICENSES
import tech.salroid.filmy.utility.OpenSourceLicense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.license))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.newtmdb),
                contentDescription = stringResource(R.string.powered_by_tmdb),
                modifier = Modifier
                    .height(88.dp)
                    .padding(16.dp)
            )

            val textColor = MaterialTheme.colorScheme.onSurface

            HtmlText(stringResource(R.string.crashlytics), textColor = textColor)

            Text(
                text = stringResource(R.string.open_source_libraries),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            )
            OPEN_SOURCE_LICENSES.forEach { license ->
                OpenSourceLicenseRow(license)
            }
        }
    }
}

@Composable
private fun OpenSourceLicenseRow(license: OpenSourceLicense) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = license.url,
                role = Role.Button,
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, license.url.toUri())) }
            )
            .padding(vertical = 8.dp)
    ) {
        Text(text = license.name, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = license.license,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    textColor: androidx.compose.ui.graphics.Color
) {
    val argbColor = textColor.toArgb()
    AndroidView(
        modifier = modifier.padding(vertical = 8.dp),
        factory = { context ->
            TextView(context).apply {
                setTextColor(argbColor)
            }
        },
        update = { textView ->
            textView.setTextColor(argbColor)
            textView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LicenseScreenPreview() {
    AppTheme {
        LicenseScreen(onBackClick = {})
    }
}
