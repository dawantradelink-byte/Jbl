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
import androidx.compose.ui.graphics.lerp
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

    // The former code animated these three skin colours but never used them in the
    // Material color scheme. Bind surfaces and accents to the selected skin too.
    val themedBackground = lerp(SurfaceDark, activeTheme.surfaceColor, 0.30f)
    val themedSurface = lerp(SurfaceContainer, activeTheme.surfaceColor, 0.34f)
    val themedSurfaceLow = lerp(SurfaceContainerLow, activeTheme.surfaceColor, 0.30f)
    val themedSurfaceHigh = lerp(SurfaceContainerHigh, activeTheme.surfaceColor, 0.34f)
    val themedSurfaceHighest = lerp(SurfaceContainerHighest, activeTheme.surfaceColor, 0.34f)

    val colorScheme = darkColorScheme(
        primary = animatedHighlight,
        onPrimary = Color(0xFF111315),
        primaryContainer = animatedPrimary,
        onPrimaryContainer = TextOnSurface,

        secondary = animatedSecondary,
        onSecondary = Color(0xFF111315),
        secondaryContainer = lerp(SurfaceContainerHigh, activeTheme.surfaceColor, 0.55f),
        onSecondaryContainer = TextOnSurface,

        tertiary = animatedHighlight,
        onTertiary = Color(0xFF111315),
        tertiaryContainer = animatedPrimary,
        onTertiaryContainer = TextOnSurface,

        background = themedBackground,
        onBackground = TextOnSurface,

        surface = themedSurface,
        onSurface = TextOnSurface,
        surfaceVariant = themedSurfaceHigh,
        onSurfaceVariant = TextOnSurfaceVariant,

        surfaceContainerLowest = lerp(SurfaceContainerLowest, activeTheme.surfaceColor, 0.14f),
        surfaceContainerLow = themedSurfaceLow,
        surfaceContainer = themedSurface,
        surfaceContainerHigh = themedSurfaceHigh,
        surfaceContainerHighest = themedSurfaceHighest,

        outline = lerp(OutlineColor, animatedHighlight, 0.16f),
        outlineVariant = lerp(OutlineVariantColor, animatedHighlight, 0.25f),

        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = themedBackground.toArgb()
                window.navigationBarColor = themedBackground.toArgb()
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
