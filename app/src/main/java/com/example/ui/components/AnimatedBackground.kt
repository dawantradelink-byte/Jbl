package com.example.ui.components

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.CrimsonRoseDim
import com.example.ui.theme.CrimsonRosePrimary
import com.example.ui.theme.DreamyPinkHighlight
import com.example.ui.theme.DreamyPinkPrimary
import com.example.ui.theme.MotionPreference
import com.example.ui.theme.OceanicBlueDim
import com.example.ui.theme.OceanicBluePrimary
import com.example.ui.theme.PrimaryContainerOceanic
import com.example.ui.theme.PrimaryMint
import com.example.ui.theme.RoastedBrownDim
import com.example.ui.theme.RoastedBrownPrimary
import com.example.ui.theme.SecondarySunsetOrange
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioTheme
import com.example.ui.theme.SunsetOrangeDim
import com.example.ui.theme.SunsetOrangeHighlight
import com.example.ui.theme.SunsetOrangePrimary
import com.example.ui.theme.TertiaryCrimsonRose
import kotlin.math.cos
import kotlin.math.sin

/**
 * Continuous Background Animation for BOOMBOX4MINI
 *
 * Combines 5 softly blended color fields:
 * - Oceanic Blue (#215C46)
 * - Roasted Brown (#45342E)
 * - Crimson Rose (#EF384A)
 * - Sunset Orange (#EE9521)
 * - Dreamy Pink (#F4D2FF / #FDACF7)
 *
 * Implements:
 * - Mathematically continuous motion via periodic trigonometric trajectories (zero boundary jumps)
 * - Lifecycle-aware animation pausing when app is not at least STARTED
 * - Respects both app MotionPreference and system ANIMATOR_DURATION_SCALE accessibility setting
 * - Pre-allocated immutable color stop lists to avoid per-frame garbage collector pressure
 * - Solid vignette protecting TalkBack, status bar, and navigation contrast
 */
@Composable
fun AnimatedBackground(
    activeTheme: StudioTheme,
    motionPreference: MotionPreference,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe lifecycle state to pause animation work when app is paused/stopped
    var isAppStarted by remember { mutableStateOf(true) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isAppStarted = event.targetState.isAtLeast(Lifecycle.State.STARTED)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Check system-level accessibility setting for animation scale
    val isSystemReducedMotion = remember(context) {
        try {
            val scale = Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
            scale == 0.0f
        } catch (_: Exception) {
            false
        }
    }

    val isReducedMotion = motionPreference == MotionPreference.REDUCED || isSystemReducedMotion

    // Target ~22 second periodic cycle (duration 22000ms) for FULL motion, 40000ms for GENTLE
    val cycleDuration = when (motionPreference) {
        MotionPreference.GENTLE -> 40000
        else -> 22000
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ambientMeshCycle")

    // Phase angle θ from 0 to 2π (6.2831855f)
    val phaseAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (!isReducedMotion && isAppStarted) (2f * Math.PI.toFloat()) else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = cycleDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseAngle"
    )

    // Secondary subtle breathing oscillation
    val breathingPulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = if (!isReducedMotion && isAppStarted) 1.06f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingPulse"
    )

    // Precomputed immutable gradient color stops (avoids per-frame allocations)
    val oceanicColors = remember {
        listOf(
            PrimaryMint.copy(alpha = 0.38f),
            OceanicBluePrimary.copy(alpha = 0.42f),
            OceanicBlueDim.copy(alpha = 0.20f),
            Color.Transparent
        )
    }
    val roastedColors = remember {
        listOf(
            RoastedBrownPrimary.copy(alpha = 0.55f),
            RoastedBrownDim.copy(alpha = 0.24f),
            Color.Transparent
        )
    }
    val crimsonColors = remember {
        listOf(
            TertiaryCrimsonRose.copy(alpha = 0.42f),
            CrimsonRosePrimary.copy(alpha = 0.36f),
            CrimsonRoseDim.copy(alpha = 0.16f),
            Color.Transparent
        )
    }
    val sunsetColors = remember {
        listOf(
            SecondarySunsetOrange.copy(alpha = 0.45f),
            SunsetOrangePrimary.copy(alpha = 0.35f),
            SunsetOrangeDim.copy(alpha = 0.12f),
            Color.Transparent
        )
    }
    val dreamyColors = remember {
        listOf(
            DreamyPinkHighlight.copy(alpha = 0.32f),
            DreamyPinkPrimary.copy(alpha = 0.16f),
            Color.Transparent
        )
    }
    val heroGlowColors = remember {
        listOf(
            SecondarySunsetOrange.copy(alpha = 0.35f),
            TertiaryCrimsonRose.copy(alpha = 0.20f),
            PrimaryContainerOceanic.copy(alpha = 0.15f),
            Color.Transparent
        )
    }
    val vignetteColors = remember {
        listOf(
            StudioBackground.copy(alpha = 0.50f),
            Color.Transparent,
            Color.Transparent,
            StudioBackground.copy(alpha = 0.85f)
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Base deep obsidian studio surface
            drawRect(color = StudioBackground)

            // 2. Field 1: Oceanic Blue (deep blue-green) in lower-left / mid-left
            val o1X = if (isReducedMotion) 0.24f else 0.22f + 0.07f * sin(phaseAngle.toDouble()).toFloat()
            val o1Y = if (isReducedMotion) 0.38f else 0.36f + 0.06f * cos(phaseAngle.toDouble()).toFloat()
            val o1Center = Offset(w * o1X, h * o1Y)
            val o1Radius = w * 0.75f * breathingPulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = oceanicColors,
                    center = o1Center,
                    radius = o1Radius
                ),
                radius = o1Radius,
                center = o1Center
            )

            // 3. Field 2: Roasted Brown (warm analog bass floor) in lower-right
            val o2X = if (isReducedMotion) 0.78f else 0.75f + 0.08f * cos(phaseAngle.toDouble()).toFloat()
            val o2Y = if (isReducedMotion) 0.74f else 0.72f + 0.07f * sin(phaseAngle.toDouble()).toFloat()
            val o2Center = Offset(w * o2X, h * o2Y)
            val o2Radius = w * 0.85f * (2f - breathingPulse)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = roastedColors,
                    center = o2Center,
                    radius = o2Radius
                ),
                radius = o2Radius,
                center = o2Center
            )

            // 4. Field 3: Crimson Rose (high-voltage acoustic energy) in upper-left
            val phaseCrimson = phaseAngle + 2.0943951f // + 2π/3
            val o3X = if (isReducedMotion) 0.28f else 0.26f + 0.08f * cos(phaseCrimson.toDouble()).toFloat()
            val o3Y = if (isReducedMotion) 0.18f else 0.18f + 0.06f * sin(phaseCrimson.toDouble()).toFloat()
            val o3Center = Offset(w * o3X, h * o3Y)
            val o3Radius = w * 0.65f * breathingPulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = crimsonColors,
                    center = o3Center,
                    radius = o3Radius
                ),
                radius = o3Radius,
                center = o3Center
            )

            // 5. Field 4: Sunset Orange (warm ambient glow behind speaker & controls)
            val phaseSunset = phaseAngle + 4.1887902f // + 4π/3
            val o4X = if (isReducedMotion) 0.68f else 0.65f + 0.09f * sin(phaseSunset.toDouble()).toFloat()
            val o4Y = if (isReducedMotion) 0.34f else 0.32f + 0.07f * cos(phaseSunset.toDouble()).toFloat()
            val o4Center = Offset(w * o4X, h * o4Y)
            val o4Radius = w * 0.78f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = sunsetColors,
                    center = o4Center,
                    radius = o4Radius
                ),
                radius = o4Radius,
                center = o4Center
            )

            // 6. Field 5: Dreamy Pink (ethereal high-frequency shimmer) central harmonic float
            val o5X = if (isReducedMotion) 0.50f else 0.50f + 0.06f * sin((2 * phaseAngle).toDouble()).toFloat()
            val o5Y = if (isReducedMotion) 0.52f else 0.52f + 0.05f * cos((2 * phaseAngle).toDouble()).toFloat()
            val o5Center = Offset(w * o5X, h * o5Y)
            val o5Radius = w * 0.56f * breathingPulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = dreamyColors,
                    center = o5Center,
                    radius = o5Radius
                ),
                radius = o5Radius,
                center = o5Center
            )

            // 7. Ambient Speaker Glow spot centered at the upper viewport
            val heroGlowCenter = Offset(w * 0.5f, h * 0.28f)
            val heroGlowRadius = w * 0.48f * breathingPulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = heroGlowColors,
                    center = heroGlowCenter,
                    radius = heroGlowRadius
                ),
                radius = heroGlowRadius,
                center = heroGlowCenter
            )

            // 8. Solid top and bottom protective vignette for WCAG 2.2 contrast
            drawRect(
                brush = Brush.verticalGradient(
                    colors = vignetteColors,
                    startY = 0f,
                    endY = h
                )
            )
        }

        // Foreground application content rendered on top of the fluid mesh
        content()
    }
}
