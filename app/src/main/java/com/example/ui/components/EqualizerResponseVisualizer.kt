package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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

/**
 * Classic studio-style moving bars plus an EQ response line. Bar heights respond to the current
 * effective 5-band profile. Motion is an ambient animation, not microphone capture or live audio FFT.
 */
@Composable
fun EqualizerResponseVisualizer(
    bandGainsDb: List<Float>,
    audioEffectAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    val safeGains = remember(bandGainsDb) {
        (0 until 5).map { index -> bandGainsDb.getOrElse(index) { 0f }.coerceIn(-12f, 12f) }
    }
    val transition = rememberInfiniteTransition(label = "eqResponseBars")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI.toFloat()),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "eqResponsePhase"
    )
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val surface = MaterialTheme.colorScheme.surface
    val curveColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val outline = OutlineVariantColor.copy(alpha = 0.48f)

    GlassCard(
        modifier = modifier.testTag("eq_response_visualizer"),
        surfaceAlpha = 0.74f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Equalizer response graph",
                        tint = primary,
                        modifier = Modifier.height(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "EQ RESPONSE VISUALIZER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.7.sp,
                            fontSize = 10.sp
                        ),
                        color = curveColor
                    )
                }
                Text(
                    text = if (audioEffectAvailable) "5-BAND PROFILE" else "PREVIEW ONLY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (audioEffectAvailable) primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(surface.copy(alpha = 0.92f))
                    .border(1.dp, primary.copy(alpha = 0.28f), RoundedCornerShape(12.dp))
                    .testTag("eq_response_canvas_container")
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(126.dp)) {
                    val w = size.width
                    val h = size.height
                    val gridCount = 4
                    for (i in 0..gridCount) {
                        val y = h * i / gridCount
                        drawLine(
                            color = outline,
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    for (i in 1..7) {
                        val x = w * i / 8f
                        drawLine(
                            color = outline.copy(alpha = 0.45f),
                            start = Offset(x, 0f),
                            end = Offset(x, h),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val baseline = h * 0.88f
                    val barCount = 32
                    val step = w / barCount
                    val gap = 2.dp.toPx().coerceAtMost(step * 0.35f)
                    val barWidth = (step - gap).coerceAtLeast(1.dp.toPx())

                    fun gainAt(position: Float): Float {
                        val scaled = position.coerceIn(0f, 1f) * 4f
                        val lower = scaled.toInt().coerceIn(0, 4)
                        val upper = (lower + 1).coerceAtMost(4)
                        val fraction = scaled - lower
                        return safeGains[lower] + (safeGains[upper] - safeGains[lower]) * fraction
                    }

                    for (i in 0 until barCount) {
                        val position = i.toFloat() / (barCount - 1)
                        val gain = gainAt(position)
                        val gainLevel = ((gain + 12f) / 24f).coerceIn(0f, 1f)
                        val wave = 0.58f + 0.42f * ((sin(phase + i * 0.58f) + 1f) / 2f)
                        val barHeight = h * (0.12f + 0.60f * gainLevel) * wave
                        val color = if (position < 0.58f) primary else secondary
                        drawRoundRect(
                            color = color.copy(alpha = 0.70f + 0.25f * wave),
                            topLeft = Offset(i * step + gap / 2f, baseline - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                        )
                    }

                    val response = Path()
                    safeGains.forEachIndexed { index, gain ->
                        val x = w * index / 4f
                        val normalized = (gain + 12f) / 24f
                        val y = h * 0.78f - normalized * h * 0.48f
                        if (index == 0) response.moveTo(x, y) else response.lineTo(x, y)
                    }
                    drawPath(
                        path = response,
                        color = curveColor,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    safeGains.forEachIndexed { index, gain ->
                        val x = w * index / 4f
                        val normalized = (gain + 12f) / 24f
                        val y = h * 0.78f - normalized * h * 0.48f
                        drawCircle(
                            color = curveColor,
                            radius = 3.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("60 Hz", "250 Hz", "1 kHz", "4 kHz", "14 kHz").forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = secondaryTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (audioEffectAvailable)
                    "Curve follows the current EQ preset and sliders."
                else
                    "Phone EQ is unavailable; this remains a profile preview.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = secondaryTextColor
            )
        }
    }
}
