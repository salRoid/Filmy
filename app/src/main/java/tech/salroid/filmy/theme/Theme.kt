package tech.salroid.filmy.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,

    secondary = Secondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    onSecondary = OnSecondary,

    tertiary = Tertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    onTertiary = OnTertiary,

    error = Error,
    onError = OnError,
    onErrorContainer = OnErrorContainer,
    errorContainer = ErrorContainer,
    inverseSurface = InverseSurface,
    inverseOnSurface = InverseOnSurface,
    scrim = Scrim,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    surface = Surface,
    surfaceBright = SurfaceBright,
    outline = Outline,
    outlineVariant = OutlineVariant,
)

@Composable
fun FilmyTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme
    ) {
        content()
    }
}