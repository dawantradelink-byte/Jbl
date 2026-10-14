package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.EqualizerPreset
import com.example.ui.components.AcousticSpeakerVisualizer
import com.example.ui.components.GlassCard
import com.example.ui.components.MediaPlaybackControls
import com.example.ui.components.TactileVolumeSlider
import com.example.ui.theme.PrimaryContainerOceanic
import com.example.ui.theme.PrimaryMint
import com.example.ui.theme.PrimaryMintDark
import com.example.ui.theme.RoastedBrownHighlight
import com.example.ui.theme.SecondaryFixedPeach
import com.example.ui.theme.SecondarySunsetContainer
import com.example.ui.theme.SecondarySunsetOrange
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TertiaryCrimsonContainer
import com.example.ui.theme.TertiaryCrimsonRose
import com.example.ui.util.LocalStudioHaptics
import com.example.viewmodel.BoomboxViewModel

@Composable
fun HomeScreen(
    viewModel: BoomboxViewModel,
    modifier: Modifier = Modifier
) {
    val haptics = LocalStudioHaptics.current
    val speakerState by viewModel.speakerState.collectAsStateWithLifecycle()
    val mediaState by viewModel.mediaState.collectAsStateWithLifecycle()
    val equalizerState by viewModel.equalizerState.collectAsStateWithLifecycle()
    val audioEffectAvailable by viewModel.audioEffectAvailable.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("home_screen")
    ) {
        // Top Header: BOOMBOX4MINI badge + 5.4 MULTI + Battery 84% + Profile Avatar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: BOOMBOX4MINI pill + 5.4 MULTI
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerHigh.copy(alpha = 0.70f))
                        .border(1.dp, PrimaryContainerOceanic.copy(alpha = 0.40f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(PrimaryMint)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = speakerState.name,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = PrimaryMint
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerLowest.copy(alpha = 0.60f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BluetoothConnected,
                            contentDescription = null,
                            tint = PrimaryMint,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "BLUETOOTH AUDIO",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Right: Battery 84% + Avatar Circle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceContainerLowest.copy(alpha = 0.60f))
                        .border(1.dp, SecondarySunsetContainer.copy(alpha = 0.30f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Battery charge",
                            tint = SecondarySunsetOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "N/A",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = SecondarySunsetOrange
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PrimaryMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Profile",
                        tint = PrimaryMintDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Speaker Hardware Card (Ambient multi-color glow + dual woofers + metrics)
        AcousticSpeakerVisualizer(
            speakerName = speakerState.name,
            connectionStatus = speakerState.connectionStatus,
            isPlaying = mediaState.isPlaying && mediaState.isMediaSessionActive,
            volumePercent = mediaState.volumePercent,
            audioEffectAvailable = audioEffectAvailable
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Honest media status: no fake track or playback controls without an active media session.
        GlassCard(
            modifier = Modifier.fillMaxWidth().testTag("media_session_status"),
            surfaceAlpha = 0.60f
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = "MEDIA PLAYBACK",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = PrimaryMint
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "No media session connected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Start audio in your preferred music app. This screen controls system volume and, when supported, Android output EQ. Track controls require a real media-session integration.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Master Gain & Dynamic Limiter
        TactileVolumeSlider(
            volumePercent = mediaState.volumePercent,
            isMuted = mediaState.isMuted,
            onVolumeChanged = { viewModel.setVolume(it) },
            onMuteToggled = { viewModel.toggleMute() }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Multi-Chromatic Sound Profile Presets (Grid of 4 mixing Sunset Orange, Oceanic Teal, Roasted Mocha, and Crimson Rose)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Equalizer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "PHONE OUTPUT EQ PRESET",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = if (audioEffectAvailable) "SYSTEM EQ READY" else "SYSTEM EQ UNAVAILABLE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = PrimaryMint
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2x2 Preset Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Preset 1: Deep Sub-Bass (Sunset Orange)
                val isPreset1Selected = equalizerState.preset == EqualizerPreset.BASS_BOOST
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerHigh.copy(alpha = if (isPreset1Selected) 0.95f else 0.70f))
                        .border(
                            1.dp,
                            if (isPreset1Selected) SecondarySunsetOrange else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(enabled = audioEffectAvailable) {
                            haptics.click()
                            viewModel.selectPreset(EqualizerPreset.BASS_BOOST)
                        }
                        .padding(10.dp)
                        .testTag("preset_subbass")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SecondarySunsetContainer.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Waves,
                                contentDescription = null,
                                tint = SecondarySunsetOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Bass Boost",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SecondarySunsetOrange
                            )
                            Text(
                                text = "60–250 Hz emphasis",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Preset 2: Oceanic Clarity (Oceanic Mint/Teal)
                val isPreset2Selected = equalizerState.preset == EqualizerPreset.VOCAL
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerHigh.copy(alpha = if (isPreset2Selected) 0.95f else 0.70f))
                        .border(
                            1.dp,
                            if (isPreset2Selected) PrimaryMint else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            haptics.click()
                            viewModel.selectPreset(EqualizerPreset.VOCAL)
                        }
                        .padding(10.dp)
                        .testTag("preset_clarity")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainerOceanic.copy(alpha = 0.40f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NaturePeople,
                                contentDescription = null,
                                tint = PrimaryMint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Oceanic Clarity",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = PrimaryMint
                            )
                            Text(
                                text = "Bright Treble / Air",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Preset 3: Warm Vinyl (Roasted Mocha)
                val isPreset3Selected = equalizerState.preset == EqualizerPreset.BALANCED
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerHigh.copy(alpha = if (isPreset3Selected) 0.95f else 0.70f))
                        .border(
                            1.dp,
                            if (isPreset3Selected) RoastedBrownHighlight else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            haptics.click()
                            viewModel.selectPreset(EqualizerPreset.BALANCED)
                        }
                        .padding(10.dp)
                        .testTag("preset_vinyl")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerLowest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Album,
                                contentDescription = null,
                                tint = SecondaryFixedPeach,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Balanced",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = SecondaryFixedPeach
                            )
                            Text(
                                text = "Flat system response",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Preset 4: Club Punch (Crimson Rose)
                val isPreset4Selected = equalizerState.preset == EqualizerPreset.CLUB
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceContainerHigh.copy(alpha = if (isPreset4Selected) 0.95f else 0.70f))
                        .border(
                            1.dp,
                            if (isPreset4Selected) TertiaryCrimsonRose else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            haptics.click()
                            viewModel.selectPreset(EqualizerPreset.CLUB)
                        }
                        .padding(10.dp)
                        .testTag("preset_punch")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(TertiaryCrimsonContainer.copy(alpha = 0.40f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = TertiaryCrimsonRose,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Club Punch",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TertiaryCrimsonRose
                            )
                            Text(
                                text = "Fast Transient",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Spacer(modifier = Modifier.height(84.dp)) // padding for floating glass bottom nav
    }
}
