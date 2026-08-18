package tech.salroid.filmy.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tech.salroid.filmy.ui.common.components.CountrySelectionList
import tech.salroid.filmy.utility.PreferenceHelper

@Composable
fun OnboardingScreen(onDone: () -> Unit) {
    val context = LocalContext.current
    var selectedCountry by remember { mutableStateOf(PreferenceHelper.getSelectedCountry(context)) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Choose your country",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 24.dp, bottom = 4.dp)
            )
            Text(
                text = "Used for accurate watch providers, ratings, and release dates.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .alpha(0.7f)
                    .padding(bottom = 20.dp)
            )

            CountrySelectionList(
                selected = selectedCountry,
                onSelect = { selectedCountry = it.code },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Button(
                onClick = {
                    PreferenceHelper.setSelectedCountry(context, selectedCountry)
                    PreferenceHelper.setColdStartDone(context)
                    onDone()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Continue")
            }
        }
    }
}
