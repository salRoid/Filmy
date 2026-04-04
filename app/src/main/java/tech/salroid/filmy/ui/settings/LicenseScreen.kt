package tech.salroid.filmy.ui.settings

import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.theme.AppTheme

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
                            contentDescription = "Back"
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
                contentDescription = "Powered by TMDB",
                modifier = Modifier
                    .height(88.dp)
                    .padding(16.dp)
            )

            val textColor = MaterialTheme.colorScheme.onSurface

            HtmlText(stringResource(R.string.materialsearch), textColor = textColor)
            HtmlText(stringResource(R.string.appintro), textColor = textColor)
            HtmlText(stringResource(R.string.crashlytics), textColor = textColor)
            HtmlText(stringResource(R.string.glide), textColor = textColor)
        }
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
