package com.welshdag.scanner.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6C5CE7),
    secondary = Color(0xFF0984E3),
    tertiary = Color(0xFF00B894),
    background = Color(0xFF1E1E2E),
    surface = Color(0xFF282A36),
    error = Color(0xFFFF6B6B)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6C5CE7),
    secondary = Color(0xFF0984E3),
    tertiary = Color(0xFF00B894),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5),
    error = Color(0xFFD32F2F)
)

@Composable
fun WelshDagTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
