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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MediaPlaybackState
import com.example.ui.theme.DreamyPinkHighlight
import com.example.ui.theme.OutlineColor
import com.example.ui.theme.OutlineVariantColor
import com.example.ui.theme.PrimaryMint
import com.example.ui.theme.PrimaryMintDark
import com.example.ui.theme.SecondarySunsetContainer
import com.example.ui.theme.SecondarySunsetOrange
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TertiaryCrimsonContainer
import com.example.ui.theme.TertiaryCrimsonRose
import com.example.ui.util.LocalStudioHaptics

@Composable
fun MediaPlaybackControls(
    mediaState: MediaPlaybackState,
    onTogglePlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleEmptyState: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalStudioHaptics.current
    var isFavorite by remember { mutableStateOf(true) }
    var isShuffleActive by remember { mutableStateOf(false) }
    var isRepeatActive by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "nowPlayingAnimation")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = if (mediaState.isPlaying && mediaState.isMediaSessionActive) 1.04f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("media_playback_card"),
        surfaceAlpha = 0.80f
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Source & Format Pill Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Stream,
                        contentDescription = null,
                        tint = PrimaryMint,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LOSSLESS MASTER STREAM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Format badge in Sunset Orange
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceContainerLowest)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "FLAC 24·48",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = SecondarySunsetOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Track Information / Empty State
            if (mediaState.isMediaSessionActive && mediaState.trackTitle != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Synthwave sunset synth mini album art
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        TertiaryCrimsonContainer,
                                        SecondarySunsetContainer,
                                        SurfaceContainerLowest
                                    )
                                )
                            )
                            .border(1.dp, SecondarySunsetOrange.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Canvas(modifier = Modifier.size(54.dp)) {
                            // Mini sunset synth grid lines
                            val w = size.width
                            val h = size.height
                            drawLine(
                                color = SecondarySunsetOrange.copy(alpha = 0.40f),
                                start = Offset(0f, h * 0.65f),
                                end = Offset(w, h * 0.65f),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = TertiaryCrimsonRose.copy(alpha = 0.50f),
                                start = Offset(0f, h * 0.80f),
                                end = Offset(w, h * 0.80f),
                                strokeWidth = 1.dp.toPx()
                            )
                            // Sun disc
                            drawCircle(
                                brush = Brush.verticalGradient(
                                    colors = listOf(SecondarySunsetOrange, TertiaryCrimsonRose)
                                ),
                                radius = 10.dp.toPx(),
                                center = Offset(w / 2f, h * 0.45f)
                            )
                        }

                        // Bottom-right pulsing live dot
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(PrimaryMint)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mediaState.trackTitle ?: "Midnight City",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f)
                            )

                            // Favorite heart toggle button
                            IconButton(
                                onClick = {
                                    haptics.click()
                                    isFavorite = !isFavorite
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isFavorite) "Favorited" else "Add to favorites",
                                    tint = if (isFavorite) TertiaryCrimsonRose else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        Text(
                            text = mediaState.artistName ?: "M83 — Hurry Up, We're Dreaming",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            } else {
                // Clear Empty State when media metadata is unavailable
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerLowest.copy(alpha = 0.70f))
                        .border(1.dp, PrimaryMint.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                        .testTag("empty_media_state")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "No Metadata",
                            tint = PrimaryMint,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "No Active Media Session",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Start playback in Spotify, YouTube Music, or any media app.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(
                            onClick = {
                                haptics.click()
                                onToggleEmptyState()
                            }
                        ) {
                            Text("Simulate", color = PrimaryMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-Chromatic Waveform Visualizer: 23 vertical bars alternating and mixing colors!
            val waveColors = remember {
                listOf(
                    PrimaryMint,
                    SecondarySunsetOrange,
                    TertiaryCrimsonRose,
                    OutlineColor,
                    PrimaryMint,
                    SecondarySunsetOrange,
                    TertiaryCrimsonRose,
                    PrimaryMint,
                    SecondarySunsetOrange,
                    OutlineColor,
                    PrimaryMint,
                    TertiaryCrimsonRose,
                    SecondarySunsetOrange,
                    PrimaryMint,
                    TertiaryCrimsonRose,
                    PrimaryMint,
                    SecondarySunsetOrange,
                    OutlineColor,
                    PrimaryMint,
                    TertiaryCrimsonRose,
                    SecondarySunsetOrange,
                    PrimaryMint,
                    OutlineColor
                )
            }
            val baseHeights = remember {
                floatArrayOf(
                    0.30f, 0.60f, 0.80f, 0.40f, 0.70f, 0.90f, 1.00f, 0.60f, 0.80f, 0.50f, 0.90f,
                    0.70f, 1.00f, 0.60f, 0.40f, 0.80f, 0.50f, 0.70f, 0.90f, 0.40f, 0.70f, 0.80f, 0.30f
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceContainerLowest.copy(alpha = 0.80f))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(28.dp)) {
                    val w = size.width
                    val h = size.height
                    val barWidth = 3.dp.toPx()
                    val totalBars = waveColors.size
                    val spacing = (w - (barWidth * totalBars)) / (totalBars - 1).coerceAtLeast(1)

                    for (i in 0 until totalBars) {
                        val factor = if (mediaState.isPlaying && mediaState.isMediaSessionActive) {
                            (baseHeights[i] * pulseScale).coerceIn(0.20f, 1.0f)
                        } else {
                            0.20f
                        }
                        val barHeight = h * factor
                        val x = i * (barWidth + spacing)
                        val y = h - barHeight

                        drawRoundRect(
                            color = waveColors[i].copy(alpha = if (mediaState.isPlaying) 0.90f else 0.40f),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-Chromatic Scrubber Bar (Primary -> Secondary -> Tertiary gradient)
            val progress = if (mediaState.durationSeconds > 0) {
                (mediaState.positionSeconds.toFloat() / mediaState.durationSeconds.toFloat()).coerceIn(0f, 1f)
            } else 0.55f

            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Track background
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(SurfaceContainerLowest)
                    ) {
                        // Multi-chromatic filled gradient
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            PrimaryMint,
                                            SecondarySunsetOrange,
                                            TertiaryCrimsonRose
                                        )
                                    )
                                )
                        )
                    }

                    // Scrubber glowing thumb
                    Box(
                        modifier = Modifier
                            .padding(start = (progress * 280).coerceIn(0f, 260f).dp)
                            .size(13.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E2E3))
                            .border(1.dp, SurfaceContainerLowest, CircleShape)
                            .shadow(8.dp, CircleShape, ambientColor = SecondarySunsetOrange, spotColor = SecondarySunsetOrange)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(if (mediaState.positionSeconds > 0) mediaState.positionSeconds else 134),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatTime(if (mediaState.durationSeconds > 0) mediaState.durationSeconds else 243),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Playback Tactile Controls (Shuffle, Previous, Master Play Hero, Next, Repeat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle button
                IconButton(
                    onClick = {
                        haptics.click()
                        isShuffleActive = !isShuffleActive
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffleActive) PrimaryMint else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Previous Track
                IconButton(
                    onClick = {
                        haptics.click()
                        onSkipPrevious()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLowest.copy(alpha = 0.50f))
                        .testTag("previous_track_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center Master Glowing Play Button (gradient from Secondary-Container via Secondary to Tertiary)
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SecondarySunsetContainer,
                                    SecondarySunsetOrange,
                                    TertiaryCrimsonRose
                                )
                            )
                        )
                        .shadow(16.dp, CircleShape, ambientColor = SecondarySunsetOrange, spotColor = SecondarySunsetOrange)
                        .clickable {
                            haptics.snap()
                            onTogglePlayPause()
                        }
                        .testTag("play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (mediaState.isPlaying && mediaState.isMediaSessionActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (mediaState.isPlaying) "Pause" else "Play",
                        tint = PrimaryMintDark,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Next Track
                IconButton(
                    onClick = {
                        haptics.click()
                        onSkipNext()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SurfaceContainerLowest.copy(alpha = 0.50f))
                        .testTag("next_track_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Repeat Single button
                IconButton(
                    onClick = {
                        haptics.click()
                        isRepeatActive = !isRepeatActive
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RepeatOne,
                        contentDescription = "Repeat Track",
                        tint = if (isRepeatActive) PrimaryMint else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
}
