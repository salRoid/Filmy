package tech.salroid.filmy.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.salroid.filmy.R
import tech.salroid.filmy.ui.account.RegionSelectionDialog
import tech.salroid.filmy.utility.PreferenceHelper
import java.util.Locale

private const val HERO_HEIGHT_FRACTION = 0.70f

@Composable
fun OnboardingScreen(
    onDone: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var selectedCountry by remember { mutableStateOf(PreferenceHelper.getSelectedCountry(context)) }
    var hasUserPickedCountry by remember { mutableStateOf(false) }
    var showCountryDialog by remember { mutableStateOf(false) }
    val posterUrls by viewModel.posterUrls.collectAsStateWithLifecycle()
    val detectedCountry by viewModel.detectedCountry.collectAsStateWithLifecycle()

    // IP-based guess is best-effort and arrives async - only apply it if the
    // user hasn't already made their own choice in the meantime.
    LaunchedEffect(detectedCountry) {
        val country = detectedCountry
        if (country != null && !hasUserPickedCountry) {
            selectedCountry = country
        }
    }

    val selectedCountryName = remember(selectedCountry) {
        Locale("", selectedCountry).displayCountry.ifBlank { selectedCountry }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedPosterBackdrop(
            posterUrls = posterUrls,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(HERO_HEIGHT_FRACTION)
                .align(Alignment.TopCenter)
        )

        // One continuous gradient spanning from well within the hero region
        // down into where the panel content starts, so the poster imagery
        // fades gradually into the solid panel color instead of hitting a
        // hard edge. The panel itself has no background of its own - by the
        // time content reaches it, this gradient is already fully solid.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        (HERO_HEIGHT_FRACTION - 0.22f) to Color.Transparent,
                        (HERO_HEIGHT_FRACTION + 0.06f) to MaterialTheme.colorScheme.background.copy(alpha = 0.45f),
                        1f to MaterialTheme.colorScheme.background.copy(alpha = 0.45f)
                    )
                )
        )

        Scaffold(containerColor = Color.Transparent) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(start = 20.dp, end = 20.dp, bottom = 16.dp)
                ) {
                    Text(
                        text = "Welcome to Filmy",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Set your country for accurate watch providers, ratings, and release dates.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .alpha(0.7f)
                            .padding(top = 4.dp, bottom = 20.dp)
                    )

                    OutlinedButton(
                        onClick = { showCountryDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = selectedCountryName,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Change country"
                        )
                    }

                    Button(
                        onClick = {
                            PreferenceHelper.setSelectedCountry(context, selectedCountry)
                            PreferenceHelper.setColdStartDone(context)
                            onDone()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text("Continue")
                    }
                }
            }
        }
    }

    if (showCountryDialog) {
        RegionSelectionDialog(
            selectedCountry = selectedCountry,
            onDismiss = { showCountryDialog = false },
            onCountrySelected = { code ->
                selectedCountry = code
                hasUserPickedCountry = true
                showCountryDialog = false
            }
        )
    }
}
