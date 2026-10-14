package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.EqualizerPreset
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.CrimsonRosePrimary
import com.example.ui.theme.DreamyPinkHighlight
import com.example.ui.theme.PrimaryContainerOceanic
import com.example.ui.theme.PrimaryMint
import com.example.ui.theme.RoastedBrownHighlight
import com.example.ui.theme.SecondarySunsetContainer
import com.example.ui.theme.SecondarySunsetOrange
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TertiaryCrimsonContainer
import com.example.ui.theme.TertiaryCrimsonRose
import com.example.ui.util.LocalStudioHaptics
import com.example.ui.util.SliderHapticTracker
import com.example.viewmodel.BoomboxViewModel

@Composable
fun AudioStudioScreen(
    viewModel: BoomboxViewModel,
    modifier: Modifier = Modifier
) {
    val haptics = LocalStudioHaptics.current
    val bassTracker = remember(haptics) {
        SliderHapticTracker(haptics, stepSize = 0.05f, centerDetent = 0.5f, minLimit = 0f, maxLimit = 1f)
    }
    val trebleTracker = remember(haptics) {
        SliderHapticTracker(haptics, stepSize = 0.05f, centerDetent = 0.5f, minLimit = 0f, maxLimit = 1f)
    }
    val bandTrackers = remember(haptics) {
        List(5) {
            SliderHapticTracker(haptics, stepSize = 1.0f, centerDetent = 0.0f, minLimit = -12f, maxLimit = 12f)
        }
    }

    val eqState by viewModel.equalizerState.collectAsStateWithLifecycle()
    val audioEffectAvailable by viewModel.audioEffectAvailable.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "eqWaveAnim")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wavePhase"
    )

    // Band colors matching the 5 palette families
    val bandColors = remember {
        listOf(
            SecondarySunsetOrange,     // 60 Hz (Sunset Orange)
            RoastedBrownHighlight,     // 250 Hz (Roasted Mocha)
            PrimaryMint,               // 1 kHz (Oceanic Mint)
            DreamyPinkHighlight,       // 4 kHz (Dreamy Pink)
            TertiaryCrimsonRose        // 14 kHz (Crimson Rose)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("audio_studio_screen")
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AUDIO STUDIO",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "PARAMETRIC EQUALIZER & DSP",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = PrimaryMint
                )
            }

            // Reset Flat Button with 48dp minimum touch target
            IconButton(
                onClick = {
                    haptics.snap()
                    viewModel.resetEqualizerFlat()
                },
                enabled = audioEffectAvailable,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.40f))
                    .border(1.dp, PrimaryMint.copy(alpha = 0.35f), CircleShape)
                    .testTag("eq_reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Reset Equalizer to 0 dB Studio Flat",
                    tint = PrimaryMint,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hardware Limitation & Honesty Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("eq_prototype_notice"),
            surfaceAlpha = 0.60f
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = PrimaryMint,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "AUDIO PROCESSING ARCHITECTURE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = PrimaryMint
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        GlassBadge(
                            text = if (audioEffectAvailable) "SYSTEM EQ ACTIVE" else "UNAVAILABLE",
                            textColor = if (audioEffectAvailable) PrimaryMint else CrimsonRosePrimary,
                            borderColor = (if (audioEffectAvailable) PrimaryMint else CrimsonRosePrimary).copy(alpha = 0.35f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "This uses Android's system output Equalizer when the phone supports a global output effect. It changes phone-side audio, not the speaker's internal DSP. If status says UNAVAILABLE, the sliders cannot affect sound on this device.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Equalizer Visual Curve Canvas with multi-color gradient
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("eq_visual_curve_card"),
            surfaceAlpha = 0.70f
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PARAMETRIC FREQUENCY RESPONSE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = eqState.preset.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = SecondarySunsetOrange
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val zeroY = h / 2f

                    // Grid reference lines: +12, 0, -12 dB
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(0f, h * 0.15f),
                        end = Offset(w, h * 0.15f),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.20f),
                        start = Offset(0f, zeroY),
                        end = Offset(w, zeroY),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(0f, h * 0.85f),
                        end = Offset(w, h * 0.85f),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Multi-chromatic Spline curve interpolated across the 5 frequency bands
                    val points = eqState.bands.mapIndexed { idx, band ->
                        val x = (idx.toFloat() / (eqState.bands.size - 1)) * (w - 32.dp.toPx()) + 16.dp.toPx()
                        val normalizedGain = (band.gainDb / 12f).coerceIn(-1f, 1f)
                        val y = zeroY - (normalizedGain * (h * 0.35f))
                        Offset(x, y)
                    }

                    val path = Path()
                    val fillPath = Path()
                    if (points.isNotEmpty()) {
                        path.moveTo(points.first().x, points.first().y)
                        fillPath.moveTo(points.first().x, zeroY)
                        fillPath.lineTo(points.first().x, points.first().y)

                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            val cx = (p0.x + p1.x) / 2f
                            path.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                            fillPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                        }

                        fillPath.lineTo(points.last().x, zeroY)
                        fillPath.close()
                    }

                    // Multi-chromatic gradient fill under the curve
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SecondarySunsetOrange.copy(alpha = 0.30f),
                                PrimaryMint.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )

                    // Spline stroke with horizontal multi-chromatic gradient
                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                SecondarySunsetOrange,
                                RoastedBrownHighlight,
                                PrimaryMint,
                                DreamyPinkHighlight,
                                TertiaryCrimsonRose
                            )
                        ),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Control anchor dots in their respective band colors
                    points.forEachIndexed { i, pt ->
                        val dotColor = bandColors.getOrElse(i) { PrimaryMint }
                        drawCircle(
                            color = dotColor,
                            radius = 5.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = SurfaceContainerLowest,
                            radius = 2.5.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Master Bass and Master Treble Controls
        Text(
            text = "ACOUSTIC TONE SHAPING",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Master Bass Control (Secondary Sunset Orange)
            GlassCard(
                modifier = Modifier.weight(1f),
                surfaceAlpha = 0.65f
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BASS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SecondarySunsetOrange
                        )
                        Text(
                            text = "${(eqState.bassLevel * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SecondarySunsetOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = eqState.bassLevel,
                        onValueChange = {
                            bassTracker.onValueChanged(it)
                            viewModel.updateBassLevel(it)
                        },
                        valueRange = 0f..1f,
                        enabled = audioEffectAvailable,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_master_bass")
                            .semantics {
                                contentDescription = "Master Bass Level"
                                stateDescription = "${(eqState.bassLevel * 100).toInt()} percent"
                            },
                        colors = SliderDefaults.colors(
                            thumbColor = SecondarySunsetOrange,
                            activeTrackColor = SecondarySunsetOrange,
                            inactiveTrackColor = SecondarySunsetContainer.copy(alpha = 0.35f)
                        )
                    )
                }
            }

            // Master Treble Control (Primary Oceanic Mint)
            GlassCard(
                modifier = Modifier.weight(1f),
                surfaceAlpha = 0.65f
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TREBLE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryMint
                        )
                        Text(
                            text = "${(eqState.trebleLevel * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryMint
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = eqState.trebleLevel,
                        onValueChange = {
                            trebleTracker.onValueChanged(it)
                            viewModel.updateTrebleLevel(it)
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_master_treble")
                            .semantics {
                                contentDescription = "Master Treble Level"
                                stateDescription = "${(eqState.trebleLevel * 100).toInt()} percent"
                            },
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryMint,
                            activeTrackColor = PrimaryMint,
                            inactiveTrackColor = PrimaryContainerOceanic.copy(alpha = 0.35f)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Selector Horizontal Scroll
        Text(
            text = "PRESET PROFILES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(EqualizerPreset.entries) { preset ->
                val isSelected = eqState.preset == preset
                val presetColor = when (preset) {
                    EqualizerPreset.BASS_BOOST -> SecondarySunsetOrange
                    EqualizerPreset.VOCAL -> PrimaryMint
                    EqualizerPreset.CLUB -> TertiaryCrimsonRose
                    EqualizerPreset.BALANCED -> RoastedBrownHighlight
                    EqualizerPreset.CUSTOM -> DreamyPinkHighlight
                }

                GlassCard(
                    modifier = Modifier
                        .testTag("preset_${preset.name.lowercase()}")
                        .semantics {
                            contentDescription = "Equalizer Preset ${preset.displayName}, ${if (isSelected) "Selected" else "Not selected"}"
                        },
                    surfaceAlpha = if (isSelected) 0.80f else 0.40f,
                    borderAlpha = if (isSelected) 0.70f else 0.20f,
                    onClick = {
                        haptics.click()
                        viewModel.selectPreset(preset)
                    }
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                        Text(
                            text = preset.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) presetColor else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (preset) {
                                EqualizerPreset.BALANCED -> "Flat curve"
                                EqualizerPreset.BASS_BOOST -> "+7.5 dB Sub"
                                EqualizerPreset.VOCAL -> "Mid boost"
                                EqualizerPreset.CLUB -> "V-shape punch"
                                EqualizerPreset.CUSTOM -> "User defined"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5-Band Interactive Equalizer Faders (Each band in its signature palette color)
        Text(
            text = "FIVE-BAND PARAMETRIC FADERS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            surfaceAlpha = 0.70f
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                eqState.bands.forEachIndexed { index, band ->
                    val color = bandColors.getOrElse(index) { PrimaryMint }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = band.frequencyLabel,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = color,
                            modifier = Modifier.width(62.dp)
                        )

                        Slider(
                            value = band.gainDb,
                            onValueChange = {
                                bandTrackers.getOrNull(band.bandIndex)?.onValueChanged(it)
                                viewModel.updateBandGain(band.bandIndex, it)
                            },
                            valueRange = -12f..12f,
                            enabled = audioEffectAvailable,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 6.dp)
                                .testTag("slider_band_${band.bandIndex}")
                                .semantics {
                                    contentDescription = "Equalizer Band ${band.frequencyLabel}"
                                    stateDescription = "%+.1f dB".format(band.gainDb)
                                },
                            colors = SliderDefaults.colors(
                                thumbColor = color,
                                activeTrackColor = color,
                                inactiveTrackColor = color.copy(alpha = 0.25f)
                            )
                        )

                        Text(
                            text = "%+.1f dB".format(band.gainDb),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = color,
                            modifier = Modifier.width(56.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(84.dp))
    }
}
