package com.example.lifeeasy.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LifeEasyColorScheme = darkColorScheme(
    primary = Primary,
    secondary = Accent,
    tertiary = Color(0xFFD0BCFF),
    background = Background,
    surface = Background,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceContainer = Color(0xFF1A1929),
    surfaceContainerHigh = Color(0xFF201F31),
    primaryContainer = Color(0xFF2D2B55),
    secondaryContainer = Color(0xFF3B2040)
)

@Composable
fun LifeEasyTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LifeEasyColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // For edge-to-edge: make system bars transparent
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = false
            insetsController.isAppearanceLightNavigationBars = false
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
