package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryElectricViolet,
    secondary = SecondaryCyberCyan,
    tertiary = VividMagenta,
    background = DeepSpaceBlack,
    surface = SpaceSlateDark,
    onPrimary = PolarWhite,
    onSecondary = DeepSpaceBlack,
    onTertiary = PolarWhite,
    onBackground = PolarWhite,
    onSurface = PolarWhite
)

// Standard beautiful light fallback scheme if user forces light theme in system settings
private val LightColorScheme = lightColorScheme(
    primary = PrimaryElectricViolet,
    secondary = SecondaryCyberCyan,
    tertiary = VividMagenta,
    background = PolarWhite,
    surface = androidx.compose.ui.graphics.Color(0xFFEFEBF7),
    onPrimary = PolarWhite,
    onSecondary = DeepSpaceBlack,
    onBackground = DeepSpaceBlack,
    onSurface = DeepSpaceBlack
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We enforce darkTheme = true or respects system so that AI Blend design stays dark neon cosmic slate luxury!
    val activeColorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Enforce glowing dark!

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = activeColorScheme.background.toArgb()
            window.navigationBarColor = activeColorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = activeColorScheme,
        typography = Typography,
        content = content
    )
}
