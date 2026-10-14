package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import android.Manifest
import android.content.pm.PackageManager
import android.media.audiofx.Visualizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlin.math.abs
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OutlineVariantColor
import kotlin.math.sin

private enum class EqStyle(val title: String) {
    SPECTRUM("Spectrum Bars"), NEON("Neon Wave"), CIRCULAR("Circular Pulse"),
    MIRROR("Mirror Peaks"), SCOPE("Retro Scope"), AURORA("Aurora Flow")
}

/** Sound-reactive visualizer using Android output-mix FFT when permission and OEM policy allow it. */
@Composable
fun EqualizerResponseVisualizer(
    bandGainsDb: List<Float>,
    audioEffectAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var styleIndex by rememberSaveable { mutableIntStateOf(0) }
    var liveRequested by rememberSaveable { mutableStateOf(false) }
    var permissionGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    var captureAvailable by remember { mutableStateOf(false) }
    var captureFailed by remember { mutableStateOf(false) }
    var fftLevels by remember { mutableStateOf(List(64) { 0.08f }) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionGranted = granted
        liveRequested = granted
        captureFailed = false
    }

    DisposableEffect(liveRequested, permissionGranted) {
        var effect: Visualizer? = null
        captureAvailable = false
        captureFailed = false
        if (liveRequested && permissionGranted) {
            try {
                val visualizer = Visualizer(0)
                visualizer.captureSize = Visualizer.getCaptureSizeRange()[1]
                visualizer.scalingMode = Visualizer.SCALING_MODE_NORMALIZED
                visualizer.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int) = Unit
                    override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
                        if (fft == null || fft.size < 8) return
                        fftLevels = List(64) { index ->
                            val bin = ((index + 1).coerceAtMost(fft.size / 2 - 1)) * 2
                            val real = fft.getOrElse(bin) { 0 }.toInt()
                            val imaginary = fft.getOrElse(bin + 1) { 0 }.toInt()
                            (kotlin.math.sqrt((real * real + imaginary * imaginary).toFloat()) / 180f).coerceIn(0.04f, 1f)
                        }
                        captureAvailable = true
                    }
                }, Visualizer.getMaxCaptureRate() / 2, false, true)
                visualizer.enabled = true
                effect = visualizer
                captureAvailable = true
            } catch (_: SecurityException) {
                captureFailed = true
                liveRequested = false
            } catch (_: RuntimeException) {
                captureFailed = true
                liveRequested = false
            }
        }
        onDispose {
            try {
                effect?.enabled = false
                effect?.release()
            } catch (_: RuntimeException) { }
        }
    }

    val gains = remember(bandGainsDb) { (0..4).map { bandGainsDb.getOrElse(it) { 0f }.coerceIn(-12f, 12f) } }
    val motion = rememberInfiniteTransition(label = "soundVisualMotion")
    val phase by motion.animateFloat(0f, 2f * Math.PI.toFloat(),
        infiniteRepeatable(tween(2300, easing = LinearEasing), RepeatMode.Restart), label = "soundVisualPhase")
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val surface = MaterialTheme.colorScheme.surface
    val textColor = MaterialTheme.colorScheme.onSurface
    val mutedText = MaterialTheme.colorScheme.onSurfaceVariant
    val live = liveRequested && permissionGranted && captureAvailable && !captureFailed
    val style = EqStyle.entries[styleIndex]

    fun ampAt(index: Int, count: Int): Float {
        if (live) return fftLevels[(index.toFloat() / (count - 1).coerceAtLeast(1) * (fftLevels.size - 1)).toInt().coerceIn(0, fftLevels.lastIndex)]
        val scaled = index.toFloat() / (count - 1).coerceAtLeast(1) * 4f
        val lo = scaled.toInt().coerceIn(0, 4)
        val hi = (lo + 1).coerceAtMost(4)
        return (0.16f + ((gains[lo] + (gains[hi] - gains[lo]) * (scaled - lo) + 12f) / 24f) * 0.48f).coerceIn(0.12f, 0.72f)
    }

    GlassCard(modifier = modifier.testTag("eq_response_visualizer"), surfaceAlpha = 0.78f) {
        Column(Modifier.fillMaxWidth().padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("SOUND VISUALIZER", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.7.sp), color = textColor)
                    Text("${styleIndex + 1}/6  •  ${style.title}", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = primary)
                }
                Box(
                    Modifier.clip(RoundedCornerShape(14.dp))
                        .background(if (live) primary.copy(alpha = 0.18f) else surface)
                        .border(1.dp, (if (live) primary else mutedText).copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                        .clickable {
                            if (live) liveRequested = false
                            else if (permissionGranted) { liveRequested = true; captureFailed = false }
                            else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("live_sound_toggle")
                ) {
                    Text(if (live) "● LIVE ON" else "♫ LIVE SOUND", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold), color = if (live) primary else mutedText)
                }
            }
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier.fillMaxWidth().height(154.dp).clip(RoundedCornerShape(14.dp))
                    .background(surface.copy(alpha = 0.96f))
                    .border(1.dp, primary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .pointerInput(styleIndex) {
                        var drag = 0f
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, amount -> drag += amount },
                            onDragEnd = {
                                if (drag < -45f) styleIndex = (styleIndex + 1) % EqStyle.entries.size
                                if (drag > 45f) styleIndex = (styleIndex - 1 + EqStyle.entries.size) % EqStyle.entries.size
                                drag = 0f
                            },
                            onDragCancel = { drag = 0f }
                        )
                    }.testTag("eq_style_swipe_canvas")
            ) {
                Canvas(Modifier.fillMaxWidth().height(154.dp)) {
                    val w = size.width
                    val h = size.height
                    val cy = h * 0.53f
                    for (i in 0..4) drawLine(mutedText.copy(alpha = 0.12f), Offset(0f, h * i / 4f), Offset(w, h * i / 4f), 1.dp.toPx())
                    for (i in 1..7) drawLine(mutedText.copy(alpha = 0.08f), Offset(w * i / 8f, 0f), Offset(w * i / 8f, h), 1.dp.toPx())
                    val count = 48
                    val step = w / count
                    when (style) {
                        EqStyle.SPECTRUM, EqStyle.MIRROR -> for (i in 0 until count) {
                            val a = ampAt(i, count)
                            val shimmer = 0.78f + 0.22f * ((sin(phase * 1.5f + i * 0.55f) + 1f) / 2f)
                            val bh = h * a * shimmer * if (style == EqStyle.MIRROR) 0.42f else 0.78f
                            val x = i * step + step * 0.12f
                            drawRoundRect(
                                color = if (i < 16) primary else if (i < 32) secondary else tertiary,
                                topLeft = Offset(x, if (style == EqStyle.MIRROR) cy - bh else cy - bh / 2f),
                                size = Size(step * 0.76f, if (style == EqStyle.MIRROR) bh * 2f else bh),
                                cornerRadius = CornerRadius(step * 0.38f)
                            )
                        }
                        EqStyle.NEON, EqStyle.AURORA, EqStyle.SCOPE -> {
                            val layers = if (style == EqStyle.SCOPE) 1 else 3
                            repeat(layers) { layer ->
                                val path = Path()
                                val points = 96
                                for (i in 0 until points) {
                                    val x = w * i / (points - 1f)
                                    val a = ampAt(i, points)
                                    val wave = sin(i * if (style == EqStyle.SCOPE) 0.31f else 0.15f + phase * (if (style == EqStyle.SCOPE) 2f else 1.1f + layer * 0.2f))
                                    val y = cy + wave * h * (0.08f + a * 0.32f) + (layer - 1) * if (layers == 1) 0f else h * 0.08f
                                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                }
                                drawPath(path, if (style == EqStyle.AURORA) listOf(tertiary, primary, secondary)[layer].copy(alpha = 0.48f) else if (layer == 0) primary else if (layer == 1) secondary else tertiary,
                                    style = Stroke(width = if (style == EqStyle.AURORA) 8.dp.toPx() else 2.4.dp.toPx(), cap = StrokeCap.Round))
                            }
                        }
                        EqStyle.CIRCULAR -> {
                            val radius = minOf(w, h) * 0.18f
                            for (i in 0 until 96) {
                                val angle = 2f * Math.PI.toFloat() * i / 96f - Math.PI.toFloat() / 2f
                                val r2 = radius + ampAt(i, 96) * h * 0.23f
                                drawLine(if (i % 3 == 0) secondary else primary,
                                    Offset(w / 2f + cos(angle) * radius, cy + sin(angle) * radius),
                                    Offset(w / 2f + cos(angle) * r2, cy + sin(angle) * r2),
                                    2.dp.toPx(), cap = StrokeCap.Round)
                            }
                            drawCircle(tertiary, radius * (0.42f + 0.08f * sin(phase)), Offset(w / 2f, cy), style = Stroke(2.dp.toPx()))
                        }
                    }
                }
                Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    EqStyle.entries.forEachIndexed { index, _ ->
                        Box(Modifier.size(if (index == styleIndex) 7.dp else 5.dp).clip(CircleShape)
                            .background(if (index == styleIndex) primary else mutedText.copy(alpha = 0.5f)))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(if (live) "Live FFT from Android audio output" else "EQ-shaped preview", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = mutedText)
                Text("SWIPE TO CHANGE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold), color = primary)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                when {
                    live -> "Sound-reactive mode is active."
                    captureFailed -> "Android/device blocked live capture. Preview is still available."
                    liveRequested -> "Connecting to audio output…"
                    else -> "Tap LIVE SOUND and allow audio permission to enable sound reaction."
                },
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = mutedText
            )
        }
    }
}
