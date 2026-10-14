package com.example.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun BoomboxTheme(
    activeTheme: StudioTheme = StudioTheme.SUNSET_ORANGE,
    content: @Composable () -> Unit
) {
    // Smooth transitions between acoustic skin profiles (350ms)
    val animatedPrimary by animateColorAsState(
        targetValue = activeTheme.primaryColor,
        animationSpec = tween(durationMillis = 350),
        label = "animPrimary"
    )
    val animatedHighlight by animateColorAsState(
        targetValue = activeTheme.highlightColor,
        animationSpec = tween(durationMillis = 350),
        label = "animHighlight"
    )
    val animatedSecondary by animateColorAsState(
        targetValue = activeTheme.accentSecondary,
        animationSpec = tween(durationMillis = 350),
        label = "animSecondary"
    )

    val colorScheme = darkColorScheme(
        // Primary: Oceanic Mint (#97D3B7) with deep container (#215C46)
        primary = PrimaryMint,
        onPrimary = PrimaryMintDark,
        primaryContainer = PrimaryContainerOceanic,
        onPrimaryContainer = PrimaryMint,

        // Secondary: Sunset Orange (#FFB86C) with warm container (#D38000)
        secondary = SecondarySunsetOrange,
        onSecondary = Color(0xFF492900),
        secondaryContainer = SecondarySunsetContainer,
        onSecondaryContainer = SecondaryFixedPeach,

        // Tertiary: Crimson Rose (#FFB3B2) with rich container (#A80025)
        tertiary = TertiaryCrimsonRose,
        onTertiary = Color(0xFF680013),
        tertiaryContainer = TertiaryCrimsonContainer,
        onTertiaryContainer = Color(0xFFFFDAD6),

        // Dark Obsidian Surfaces
        background = SurfaceDark,
        onBackground = TextOnSurface,

        surface = SurfaceContainer,
        onSurface = TextOnSurface,
        surfaceVariant = SurfaceContainerHigh,
        onSurfaceVariant = TextOnSurfaceVariant,

        surfaceContainerLowest = SurfaceContainerLowest,
        surfaceContainerLow = SurfaceContainerLow,
        surfaceContainer = SurfaceContainer,
        surfaceContainerHigh = SurfaceContainerHigh,
        surfaceContainerHighest = SurfaceContainerHighest,

        outline = OutlineColor,
        outlineVariant = OutlineVariantColor,

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = SurfaceDark.toArgb()
                window.navigationBarColor = SurfaceDark.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
