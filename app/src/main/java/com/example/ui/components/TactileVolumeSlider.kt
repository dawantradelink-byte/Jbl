package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.PrimaryContainerOceanic
import com.example.ui.theme.SecondarySunsetOrange
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TertiaryCrimsonContainer
import com.example.ui.theme.TertiaryCrimsonRose
import com.example.ui.util.LocalStudioHaptics
import com.example.ui.util.SliderHapticTracker

@Composable
fun TactileVolumeSlider(
    volumePercent: Int,
    isMuted: Boolean,
    onVolumeChanged: (Int) -> Unit,
    onMuteToggled: () -> Unit,
    volumeLimiterEnabled: Boolean = true,
    onMaxOutputRequested: () -> Unit = {},
    onSafeLimitRequested: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptics = LocalStudioHaptics.current
    val hapticTracker = remember(haptics) {
        SliderHapticTracker(
            haptics = haptics,
            stepSize = 2.0f,
            centerDetent = 50f,
            minLimit = 0f,
            maxLimit = 100f
        )
    }

    val currentVolume by rememberUpdatedState(volumePercent)
    val currentOnVolumeChanged by rememberUpdatedState(onVolumeChanged)

    val dbReadout = when {
        isMuted -> "-inf dB"
        volumePercent == 0 -> "-inf dB"
        volumePercent >= 90 -> "+3.0 dB"
        volumePercent >= 75 -> "+2.5 dB"
        volumePercent >= 50 -> "0 dB"
        else -> "-${(50 - volumePercent) / 3} dB"
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tactile_volume_card"),
        surfaceAlpha = 0.70f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Icon + MASTER OUTPUT GAIN + Readout & LIMIT SAFE badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        haptics.click()
                        onMuteToggled()
                    }
                ) {
                    Icon(
                        imageVector = when {
                            isMuted -> Icons.Default.VolumeOff
                            volumePercent == 0 -> Icons.Default.VolumeMute
                            volumePercent < 50 -> Icons.Default.VolumeDown
                            else -> Icons.Default.VolumeUp
                        },
                        contentDescription = if (isMuted) "Unmute" else "Mute",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MASTER OUTPUT GAIN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Readout in Secondary Sunset Orange
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceContainerLowest)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isMuted) "MUTED" else "$volumePercent% • $dbReadout",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // LIMIT SAFE badge in outline
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceContainerLowest)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (volumePercent > 85) "DYNAMIC COMP" else "LIMIT SAFE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = OutlineColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Precision Gain Slider with Multi-Chromatic Gradient Fill
            var dragOffset by remember { mutableFloatStateOf(0f) }
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("volume_slider_container")
                    .pointerInput(volumePercent) {
                        detectTapGestures { tap ->
                            val fraction = (tap.x / size.width.toFloat()).coerceIn(0f, 1f)
                            val newVolume = (fraction * 100f).toInt()
                            hapticTracker.onValueChanged(newVolume.toFloat())
                            currentOnVolumeChanged(newVolume)
                        }
                    }
                    .pointerInput(constraints.maxWidth) {
                        detectDragGestures(
                            onDragStart = { position ->
                                dragOffset = position.x
                                val newVolume = ((position.x / size.width.toFloat()).coerceIn(0f, 1f) * 100f).toInt()
                                hapticTracker.onValueChanged(newVolume.toFloat())
                                currentOnVolumeChanged(newVolume)
                            },
                            onDrag = { change, amount ->
                                change.consume()
                                dragOffset = (dragOffset + amount.x).coerceIn(0f, size.width.toFloat())
                                val newVolume = ((dragOffset / size.width.toFloat()).coerceIn(0f, 1f) * 100f).toInt()
                                if (newVolume != currentVolume) {
                                    hapticTracker.onValueChanged(newVolume.toFloat())
                                    currentOnVolumeChanged(newVolume)
                                }
                            }
                        )
                    }
                    .semantics {
                        role = Role.Button
                        contentDescription = "Master Volume Gain"
                        stateDescription = if (isMuted) "Muted" else "$volumePercent percent, $dbReadout"
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                val effectiveFraction = if (isMuted) 0f else (volumePercent / 100f).coerceIn(0f, 1f)

                // Track background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceContainerLowest)
                ) {
                    // Multi-chromatic filled gradient: Primary Container -> Secondary -> Tertiary Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(effectiveFraction)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                    )
                }

                // Smooth, animated precision knob
                val targetThumbOffset = (maxWidth * effectiveFraction - 12.dp)
                    .coerceIn(0.dp, (maxWidth - 24.dp).coerceAtLeast(0.dp))
                val thumbOffset by animateDpAsState(
                    targetValue = targetThumbOffset,
                    animationSpec = spring(dampingRatio = 0.82f, stiffness = 650f),
                    label = "volumeKnobPosition"
                )

                Box(
                    modifier = Modifier
                        .offset(x = thumbOffset)
                        .size(width = 24.dp, height = 30.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .border(2.dp, MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f), CircleShape)
                        .shadow(12.dp, CircleShape, ambientColor = MaterialTheme.colorScheme.primary, spotColor = MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    // Center grip notch
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(MaterialTheme.colorScheme.onPrimary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Audio Decibel Hash Scale
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("-inf", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = OutlineColor)
                Text("-18", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = OutlineColor)
                Text("-12", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = OutlineColor)
                Text("-6", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = OutlineColor)
                Text("0 dB", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = OutlineColor)

                // +3 CLIP in Tertiary Crimson Rose with live dot
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "+3 CLIP",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explicit, honest output modes: maximum Android stream volume vs the app's 85% limiter.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (!volumeLimiterEnabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                            else SurfaceContainerLowest
                        )
                        .border(
                            1.dp,
                            if (!volumeLimiterEnabled) MaterialTheme.colorScheme.primary
                            else OutlineColor.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onMaxOutputRequested() }
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                        .testTag("max_output_mode")
                ) {
                    Column {
                        Text(
                            text = if (!volumeLimiterEnabled) "MAX OUTPUT ACTIVE" else "MAX OUTPUT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Allow up to 100%",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (volumeLimiterEnabled) MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f)
                            else SurfaceContainerLowest
                        )
                        .border(
                            1.dp,
                            if (volumeLimiterEnabled) MaterialTheme.colorScheme.secondary
                            else OutlineColor.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { onSafeLimitRequested() }
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                        .testTag("safe_volume_mode")
                ) {
                    Column {
                        Text(
                            text = if (volumeLimiterEnabled) "85% SAFE LIMIT ACTIVE" else "85% SAFE LIMIT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Limit app volume to 85%",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Stepping Buttons (-5% / +5%) for accessibility and tactile control
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        val newVol = (volumePercent - 5).coerceAtLeast(0)
                        hapticTracker.onValueChanged(newVol.toFloat())
                        onVolumeChanged(newVol)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLowest)
                        .testTag("volume_down_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeDown,
                        contentDescription = "Decrease volume by 5 percent",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "System Volume Synchronization",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                IconButton(
                    onClick = {
                        val newVol = (volumePercent + 5).coerceAtMost(100)
                        hapticTracker.onValueChanged(newVol.toFloat())
                        onVolumeChanged(newVol)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLowest)
                        .testTag("volume_up_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Increase volume by 5 percent",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
