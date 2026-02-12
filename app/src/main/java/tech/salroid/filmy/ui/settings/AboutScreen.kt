package tech.salroid.filmy.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil3.compose.AsyncImage
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.theme.AppTheme
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(top = 12.dp),
                        text = stringResource(R.string.about_screen_tittle)
                    )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DeveloperCard(
                name = stringResource(R.string.name_webianks),
                bio = stringResource(R.string.bio_webianks),
                bannerUrl = stringResource(R.string.banner_webianks),
                profileUrl = stringResource(R.string.profile_webianks),
                email = "webianks@gmail.com",
                githubUrl = stringResource(R.string.git_webianks),
                websiteUrl = stringResource(R.string.website_webianks)
            )

            DeveloperCard(
                name = stringResource(R.string.name_salroid),
                bio = stringResource(R.string.bio_salroid),
                bannerUrl = stringResource(R.string.banner_salroid),
                profileUrl = stringResource(R.string.profile_salroid),
                email = "gupta.sajal631@gmail.com",
                githubUrl = stringResource(R.string.git_salroid),
                websiteUrl = stringResource(R.string.website_salroid)
            )
        }
    }
}

@Composable
fun DeveloperCard(
    name: String,
    bio: String,
    bannerUrl: String,
    profileUrl: String,
    email: String,
    githubUrl: String,
    websiteUrl: String
) {
    val context = LocalContext.current

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box(modifier = Modifier.height(175.dp)) {
                AsyncImage(
                    model = bannerUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.default_banner_min)
                )

                AsyncImage(
                    model = profileUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 55.dp)
                        .size(120.dp)
                        .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.default_avatar)
                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge
                        .copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:$email")
                        }
                        context.startActivity(Intent.createChooser(emailIntent, "Send feedback"))
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.email), fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = {
                        openCustomTabIntent(context, githubUrl, android.R.color.black)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.github), fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = {
                        openCustomTabIntent(context, websiteUrl, R.color.colorMore)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.website), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun openCustomTabIntent(context: Context, url: String, colorRes: Int) {
    val intent = CustomTabsIntent.Builder().run {
        val customTabColorSchemeParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ContextCompat.getColor(context, colorRes))
            .build()
        setDefaultColorSchemeParams(customTabColorSchemeParams)
        build()
    }
    intent.launchUrl(context, url.toUri())
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AppTheme {
        AboutScreen(onBackClick = {})
    }
}
