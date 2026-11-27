package com.moma.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MomaLightColors = lightColorScheme(
    primary = Green700,
    onPrimary = Surface,
    primaryContainer = Green100,
    onPrimaryContainer = Green900,
    secondary = Green600,
    onSecondary = Surface,
    secondaryContainer = Green50,
    onSecondaryContainer = Green800,
    tertiary = Amber600,
    onTertiary = Surface,
    tertiaryContainer = Amber100,
    onTertiaryContainer = Ink900,
    background = Background,
    onBackground = Ink900,
    surface = Surface,
    onSurface = Ink900,
    surfaceVariant = Green50,
    onSurfaceVariant = Ink500,
    outline = Line,
    outlineVariant = Line,
    error = Red600,
    onError = Surface,
    errorContainer = Red100,
    onErrorContainer = Red600
)

/**
 * MoMA is intentionally a single light theme: gym staff use it matside and in
 * bright gyms, and the documented design system has one surface scale.
 */
@Composable
fun MomaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MomaLightColors,
        typography = MomaTypography,
        content = content
    )
}
