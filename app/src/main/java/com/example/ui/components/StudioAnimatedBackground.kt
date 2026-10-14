package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ui.theme.MotionPreference
import com.example.ui.theme.StudioTheme

/**
 * StudioAnimatedBackground delegates to AnimatedBackground, which uses
 * Compose Canvas and Animatable for smooth continuous floating gradient orbs.
 */
@Composable
fun StudioAnimatedBackground(
    activeTheme: StudioTheme,
    motionPreference: MotionPreference,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedBackground(
        activeTheme = activeTheme,
        motionPreference = motionPreference,
        modifier = modifier,
        content = content
    )
}
