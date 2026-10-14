package com.example.data

import android.media.audiofx.Equalizer
import kotlin.math.abs

/**
 * Applies a real Android output-mix equalizer when the device supports it.
 * This changes phone-side audio output; it does not write DSP settings into a Bluetooth speaker.
 */
class SystemEqualizerHelper {
    private var effect: Equalizer? = try {
        Equalizer(0, 0).apply { enabled = true }
    } catch (_: Exception) {
        null
    }

    val isAvailable: Boolean
        get() = effect != null

    fun applyFiveBandCurve(gainsDb: List<Float>): Boolean {
        val eq = effect ?: return false
        val targetFrequenciesHz = listOf(60, 250, 1000, 4000, 14000)
        try {
            val bandCount = eq.numberOfBands.toInt()
            if (bandCount < targetFrequenciesHz.size) {
                release()
                return false
            }
            val range = eq.bandLevelRange
            targetFrequenciesHz.forEachIndexed { targetIndex, frequencyHz ->
                val targetMilliHz = frequencyHz * 1000
                var nearestBand = 0
                var nearestDistance = Int.MAX_VALUE
                for (band in 0 until bandCount) {
                    val centerHz = eq.getCenterFreq(band.toShort())
                    val distance = abs(centerHz - targetMilliHz)
                    if (distance < nearestDistance) {
                        nearestDistance = distance
                        nearestBand = band
                    }
                }
                val gain = gainsDb.getOrElse(targetIndex) { 0f }
                val level = (gain * 100f).toInt().coerceIn(range[0].toInt(), range[1].toInt())
                eq.setBandLevel(nearestBand.toShort(), level.toShort())
            }
            eq.enabled = true
            return true
        } catch (_: Exception) {
            // Effect support varies by Android device and audio HAL. Never claim the curve was applied.
            release()
            return false
        }
    }

    fun release() {
        try {
            effect?.release()
        } catch (_: Exception) {
            // Ignore cleanup failures from device audio effects.
        } finally {
            effect = null
        }
    }
}
