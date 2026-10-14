package com.example.ui.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import kotlin.math.abs

/**
 * Calibrated tactile haptic feedback engine for BOOMBOX Control Studio.
 * Provides differentiated physical sensations for faders, rotary controls,
 * hardware toggles, and transport buttons.
 */
class StudioHaptics(
    private val context: Context?,
    private val view: View?,
    val enabled: Boolean = true
) {
    private val vibrator: Vibrator? by lazy {
        try {
            val ctx = context ?: return@lazy null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Standard tactile click for buttons, tabs, input switches, and menu items.
     */
    fun click() {
        if (!enabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20L, 160))
            } else {
                view?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
        } catch (e: Exception) {
            view?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    /**
     * Subtle, low-inertia tick for fine slider increments and rotary stepping.
     */
    fun tick() {
        if (!enabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(10L, 80))
            } else {
                view?.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }
        } catch (e: Exception) {
            view?.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }

    /**
     * Sharp, pronounced tactile snap for center detents (0 dB / 50%),
     * limits (0% / 100%), Bluetooth connection state transitions, and equalizer preset selections.
     */
    fun snap() {
        if (!enabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(35L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
        } catch (e: Exception) {
            view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }

    /**
     * Mechanical toggle feel for safety limiter and haptic preference switches.
     */
    fun toggle() {
        if (!enabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                view?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            }
        } catch (e: Exception) {
            view?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    /**
     * Warning or alert pulse (e.g. acoustic limiter triggered or scan error).
     */
    fun warning() {
        if (!enabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0L, 25L, 35L, 25L)
                val amplitudes = intArrayOf(0, 180, 0, 180)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
        } catch (e: Exception) {
            view?.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }
}

/**
 * Monitors continuous float value changes on sliders (Volume, Master Bass/Treble,
 * 5-Band EQ) and triggers haptic ticks at step intervals and tactile snaps at
 * detents (center 0dB/50%, min/max limits).
 */
class SliderHapticTracker(
    private val haptics: StudioHaptics,
    val stepSize: Float,
    val centerDetent: Float? = null,
    val minLimit: Float? = null,
    val maxLimit: Float? = null,
    private val minIntervalMs: Long = 28L
) {
    private var lastValue: Float? = null
    private var lastTickTime: Long = 0L

    fun onValueChanged(newValue: Float) {
        val previous = lastValue
        lastValue = newValue
        if (previous == null) return

        val now = System.currentTimeMillis()
        if (now - lastTickTime < minIntervalMs) return

        // 1. Center detent snap (e.g. 0 dB flat or 50% midpoint)
        if (centerDetent != null) {
            val crossedCenter = (previous < centerDetent && newValue >= centerDetent) ||
                    (previous > centerDetent && newValue <= centerDetent)
            if (crossedCenter) {
                haptics.snap()
                lastTickTime = now
                return
            }
        }

        // 2. Bound limit snap (reaching absolute minimum or maximum)
        if (minLimit != null && previous > minLimit && newValue <= minLimit) {
            haptics.snap()
            lastTickTime = now
            return
        }
        if (maxLimit != null && previous < maxLimit && newValue >= maxLimit) {
            haptics.snap()
            lastTickTime = now
            return
        }

        // 3. Regular step interval tick
        val diff = abs(newValue - previous)
        if (diff >= stepSize) {
            haptics.tick()
            lastTickTime = now
        }
    }
}

/**
 * CompositionLocal providing [StudioHaptics] down the Compose tree.
 */
val LocalStudioHaptics = staticCompositionLocalOf {
    StudioHaptics(context = null, view = null, enabled = false)
}

/**
 * Creates and remembers a [StudioHaptics] instance bound to the current Context and View.
 */
@Composable
fun rememberStudioHaptics(enabled: Boolean = true): StudioHaptics {
    val context = LocalContext.current
    val view = LocalView.current
    return remember(context, view, enabled) {
        StudioHaptics(context = context, view = view, enabled = enabled)
    }
}
