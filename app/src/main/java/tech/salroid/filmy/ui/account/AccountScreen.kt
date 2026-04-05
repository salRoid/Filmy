package tech.salroid.filmy.ui.account

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tech.salroid.filmy.BuildConfig
import tech.salroid.filmy.R
import tech.salroid.filmy.data.local.db.entity.Profile
import tech.salroid.filmy.ui.home.LoginViewModel
import tech.salroid.filmy.utility.FilmyUtility
import tech.salroid.filmy.utility.PreferenceHelper
import androidx.core.net.toUri
import androidx.compose.ui.tooling.preview.Preview
import tech.salroid.filmy.ui.theme.AppTheme

@Composable
fun AccountScreen(
    onAboutClick: () -> Unit,
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiStateProfile by viewModel.uiStateProfile.collectAsState()
    val uiStateToken by viewModel.uiStateToken.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var rootSize by remember { mutableStateOf(IntSize.Zero) }
    var cardSize by remember { mutableStateOf(IntSize.Zero) }

    val customTabLauncher = rememberLauncherForActivityResult(
        contract = object : ActivityResultContract<String, Int>() {
            override fun createIntent(context: Context, input: String): Intent {
                val builder = CustomTabsIntent.Builder()
                    .setInitialActivityHeightPx(rootSize.height - cardSize.height)
                val customTabsIntent = builder.build().intent
                customTabsIntent.data = input.toUri()
                return customTabsIntent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Int = resultCode
        }
    ) { }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (viewModel.requestToken != null && viewModel.sessionId == null) {
                    viewModel.getAccessToken()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiStateToken) {
        uiStateToken?.requestToken?.let { token ->
            if (viewModel.accessToken == null) {
                val url = "https://www.themoviedb.org/auth/access?request_token=$token"
                customTabLauncher.launch(url)
            }
        }
    }

    LaunchedEffect(uiStateProfile) {
        isLoading = false
    }

    AccountScreenContent(
        profile = uiStateProfile,
        isLoading = isLoading,
        onLoginClick = {
            isLoading = true
            viewModel.getRequestToken()
        },
        onLogoutClick = {
            showLogoutConfirmation(context) {
                isLoading = true
                viewModel.logout()
            }
        },
        onAboutClick = onAboutClick,
        onLicenseClick = onLicenseClick,
        modifier = modifier,
        onRootSizeChanged = { rootSize = it },
        onCardSizeChanged = { cardSize = it }
    )
}

@Composable
fun AccountScreenContent(
    profile: Profile?,
    isLoading: Boolean,
    showLoginCard: Boolean = false,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAboutClick: () -> Unit,
    onLicenseClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRootSizeChanged: (IntSize) -> Unit = {},
    onCardSizeChanged: (IntSize) -> Unit = {}
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .onGloballyPositioned { onRootSizeChanged(it.size) }
    ) {
        if (showLoginCard) {
            LoginCard(
                profile = profile,
                onLoginClick = onLoginClick,
                onLogoutClick = onLogoutClick,
                isLoading = isLoading,
                modifier = Modifier.onGloballyPositioned { onCardSizeChanged(it.size) }
            )
        }

        PreferencesSection(
            context = context,
            onAboutClick = onAboutClick,
            onLicenseClick = onLicenseClick
        )
    }
}

@Composable
fun PreferencesSection(
    context: Context,
    onAboutClick: () -> Unit,
    onLicenseClick: () -> Unit
) {
    var showThemeDialog by remember { mutableStateOf(false) }

    val modeNightNo = stringResource(R.string.mode_night_no)
    val modeNightYes = stringResource(R.string.mode_night_yes)

    val currentThemeMode = PreferenceHelper.getCurrentThemeMode(context)
    val currentThemeSummary = when (currentThemeMode) {
        modeNightNo -> stringResource(R.string.summary_light)
        modeNightYes -> stringResource(R.string.summary_dark)
        else -> stringResource(R.string.summary_system_default)
    }

    Column(modifier = Modifier.padding(8.dp)) {
        PreferenceItem(
            title = stringResource(R.string.theme),
            summary = currentThemeSummary,
            icon = painterResource(R.drawable.dark_mode),
            onClick = { showThemeDialog = true }
        )
        PreferenceItem(
            title = stringResource(R.string.license),
            icon = painterResource(R.drawable.ic_article),
            onClick = onLicenseClick
        )
        PreferenceItem(
            title = stringResource(R.string.version),
            summary = BuildConfig.VERSION_NAME,
            icon = painterResource(R.drawable.ic_document),
            onClick = {}
        )
        PreferenceItem(
            title = stringResource(R.string.shareappdetails),
            icon = painterResource(R.drawable.twotone_share_24),
            onClick = { FilmyUtility.startSharingIntent(context) }
        )
        PreferenceItem(
            title = stringResource(R.string.about),
            icon = painterResource(R.drawable.ic_face),
            onClick = onAboutClick
        )
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentThemeMode = currentThemeMode,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { selectedValue ->
                PreferenceHelper.setThemeMode(context, selectedValue)
                val nightMode = when (selectedValue) {
                    modeNightNo -> AppCompatDelegate.MODE_NIGHT_NO
                    modeNightYes -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                AppCompatDelegate.setDefaultNightMode(nightMode)
                showThemeDialog = false
            }
        )
    }
}

@Composable
fun PreferenceItem(
    title: String,
    summary: String? = null,
    icon: Painter? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentThemeMode: String?,
    onDismiss: () -> Unit,
    onThemeSelected: (String) -> Unit
) {
    val entries = stringArrayResource(R.array.themeEntries)
    val values = stringArrayResource(R.array.themeValues)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.theme),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(Modifier.selectableGroup()) {
                entries.forEachIndexed { index, item ->
                    val value = values[index]
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (value == currentThemeMode),
                                onClick = { onThemeSelected(value) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (value == currentThemeMode),
                            onClick = null
                        )
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    )
}

@Composable
fun LoginCard(
    profile: Profile?,
    onLoginClick: () -> Unit,
    onLogoutClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .dropShadow(
                shape = RoundedCornerShape(12.dp),
                shadow = Shadow(
                    radius = 16.dp,
                    spread = 0.dp,
                    offset = DpOffset(0.dp, 4.dp),
                    alpha = 0.06f,
                    color = MaterialTheme.colorScheme.scrim
                )
            ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val avatarUrl = profile?.let {
                    it.avatar?.tmdb?.avatarPath?.let { path ->
                        stringResource(R.string.member_profile_url, path)
                    } ?: it.avatar?.gravatar?.getCompleteUrl()
                }

                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.default_avatar),
                    error = painterResource(R.drawable.default_avatar),
                    modifier = Modifier
                        .size(62.dp)
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1.0f)
                ) {
                    Text(
                        text = profile?.name ?: stringResource(R.string.login_message),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.alpha(0.9f)
                    )
                    Text(
                        text = profile?.username ?: stringResource(R.string.login_message_benefit),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.alpha(0.6f)
                    )

                    if (profile == null) {
                        Button(
                            onClick = onLoginClick,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .width(100.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            enabled = !isLoading
                        ) {
                            Text(text = stringResource(R.string.login_now))
                        }
                    }
                }

                if (profile != null) {
                    IconButton(
                        onClick = onLogoutClick,
                        enabled = !isLoading
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun showLogoutConfirmation(context: Context, onConfirm: () -> Unit) {
    MaterialAlertDialogBuilder(context)
        .setMessage(context.getString(R.string.logout_confirmation))
        .setNegativeButton(R.string.yes) { _, _ ->
            onConfirm()
        }
        .setPositiveButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }.show()
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    AppTheme {
        AccountScreenContent(
            profile = Profile(
                name = "John Doe",
                username = "johndoe123"
            ),
            isLoading = false,
            onLoginClick = {},
            onLogoutClick = {},
            onAboutClick = {},
            onLicenseClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenLoadingPreview() {
    AppTheme {
        AccountScreenContent(
            profile = null,
            isLoading = true,
            onLoginClick = {},
            onLogoutClick = {},
            onAboutClick = {},
            onLicenseClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenLoggedOutPreview() {
    AppTheme {
        AccountScreenContent(
            profile = null,
            isLoading = false,
            onLoginClick = {},
            onLogoutClick = {},
            onAboutClick = {},
            onLicenseClick = {}
        )
    }
}
