package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JflyDarkColorScheme = darkColorScheme(
    primary = JflyGold,
    onPrimary = DarkBg,
    primaryContainer = JflyGoldDark,
    onPrimaryContainer = TextPrimary,
    secondary = AccentGreen,
    onSecondary = DarkBg,
    tertiary = NeonCyan,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    error = AccentRed,
    onError = TextPrimary
)

@Composable
fun JflyFootballTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JflyDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    JflyFootballTheme(darkTheme, dynamicColor, content)
}
