package com.github.mrbean355.admiralbulldog.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = TwitchPurple,
    onPrimary = TwitchHighEmphasis,
    primaryContainer = TwitchPurpleLow,
    onPrimaryContainer = TwitchHighEmphasis,
    secondary = TwitchCyan,
    onSecondary = TwitchDarkBackground,
    background = TwitchDarkBackground,
    onBackground = TwitchHighEmphasis,
    surface = TwitchDarkSurface,
    onSurface = TwitchHighEmphasis,
    surfaceVariant = TwitchDarkSurfaceVariant,
    onSurfaceVariant = TwitchMediumEmphasis,
    error = TwitchError,
    onError = TwitchHighEmphasis
)

val BulldogShapes = Shapes(
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
)

@Composable
fun BulldogTheme(
    content: @Composable () -> Unit
) {
    // Hardcoded to dark mode as per user request.
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = BulldogTypography,
        shapes = BulldogShapes,
        content = content
    )
}
