package com.henan.learning.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryGreen.copy(alpha = 0.1f),
    secondary = PrimaryGold,
    onSecondary = OnPrimaryDark,
    tertiary = PrimaryOrange,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryGreen.copy(alpha = 0.2f),
    secondary = PrimaryGold,
    onSecondary = OnPrimaryDark,
    tertiary = PrimaryRed,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = OnPrimaryLight,
    onSurface = OnPrimaryLight,
    error = ErrorRed
)

@Composable
fun HenanLearningTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
