package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.MotionPreference
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusDisconnected
import com.example.ui.theme.StudioTheme
import com.example.ui.util.LocalStudioHaptics
import com.example.viewmodel.BoomboxViewModel

data class CapabilityItem(
    val title: String,
    val description: String,
    val isSupported: Boolean,
    val note: String
)

@Composable
fun SettingsScreen(
    viewModel: BoomboxViewModel,
    modifier: Modifier = Modifier
) {
    val haptics = LocalStudioHaptics.current
    val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()
    val motionPref by viewModel.motionPreference.collectAsStateWithLifecycle()
    val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()
    val highlightColor = MaterialTheme.colorScheme.primary
    val primaryColor = MaterialTheme.colorScheme.primaryContainer

    var expandedTroubleshootingIndex by remember { mutableStateOf<Int?>(null) }

    val capabilities = remember {
        listOf(
            CapabilityItem(
                title = "A2DP Bluetooth Audio Streaming",
                description = "Android routes audio to connected Bluetooth A2DP devices",
                isSupported = true,
                note = "Android Bluetooth audio profile"
            ),
            CapabilityItem(
                title = "AVRCP Media Transport Controls",
                description = "Play, Pause, Next, Previous track through an Android media session",
                isSupported = false,
                note = "Not implemented; use your music app controls"
            ),
            CapabilityItem(
                title = "System Volume Synchronization",
                description = "Read and change Android music-stream volume",
                isSupported = true,
                note = "Android AudioManager"
            ),
            CapabilityItem(
                title = "Hardware Battery Telemetry",
                description = "Battery percentage reported by the connected speaker",
                isSupported = false,
                note = "Not exposed by the current A2DP-only integration; not verified for every device"
            ),
            CapabilityItem(
                title = "Proprietary Equalizer Firmware Flashing",
                description = "Changing the speaker internal DSP or firmware EQ",
                isSupported = false,
                note = "No documented speaker-specific control protocol is integrated"
            ),
            CapabilityItem(
                title = "Over-The-Air (OTA) Firmware Updates",
                description = "Updating internal speaker firmware from this app",
                isSupported = false,
                note = "Not implemented; requires a documented vendor update protocol"
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("settings_screen")
    ) {
        // Screen Header
        Text(
            text = "SETTINGS & SYSTEM",
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "THEMES, MOTION & ACCESSIBILITY",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            ),
            color = highlightColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Unified 5-Color Family Palette Showcase with Acoustic Accent Focus
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = highlightColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "ACOUSTIC ACCENT FOCUS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StudioTheme.selectableThemes.forEach { theme ->
                val isSelected = activeTheme.id == theme.id ||
                        (theme == StudioTheme.SUNSET_ORANGE && activeTheme == StudioTheme.SUNSET_GLOW) ||
                        (theme == StudioTheme.DREAMY_PINK && activeTheme == StudioTheme.DEEP_PLUM)

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("theme_card_${theme.id}")
                        .semantics {
                            role = Role.RadioButton
                            stateDescription = if (isSelected) "Active Theme" else "Inactive"
                            contentDescription = "${theme.title} Theme: ${theme.subtitle}"
                        },
                    surfaceAlpha = if (isSelected) 0.70f else 0.40f,
                    borderAlpha = if (isSelected) 0.65f else 0.20f,
                    onClick = {
                        haptics.snap()
                        viewModel.selectTheme(theme)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Swatch preview circle pair
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(theme.primaryColor)
                                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                    .background(theme.highlightColor)
                                    .border(0.5.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = theme.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) highlightColor else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = theme.subtitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isSelected) {
                            GlassBadge(
                                text = "ACTIVE",
                                textColor = highlightColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Animated Background Motion Preference
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Animation,
                contentDescription = null,
                tint = highlightColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "CONTINUOUS BACKGROUND MOTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            MotionPreference.entries.forEach { pref ->
                val isSelected = motionPref == pref
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("motion_${pref.name.lowercase()}")
                        .semantics {
                            role = Role.RadioButton
                            stateDescription = if (isSelected) "Selected" else "Not selected"
                            contentDescription = "${pref.title}: ${pref.description}"
                        },
                    surfaceAlpha = if (isSelected) 0.65f else 0.35f,
                    borderAlpha = if (isSelected) 0.60f else 0.18f,
                    onClick = {
                        haptics.click()
                        viewModel.selectMotionPreference(pref)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = pref.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isSelected) highlightColor else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = pref.description,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(highlightColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Studio Preferences (Volume Limiter, Haptics)
        Text(
            text = "AUDIO SAFETY & TACTILE CONTROLS",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            surfaceAlpha = 0.65f
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Volume Limiter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = highlightColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Acoustic Safety Limiter (85%)",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Caps volume changes made in this app at 85%. Android hardware buttons can still change system volume.",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = settingsState.volumeLimiterEnabled,
                        onCheckedChange = {
                            haptics.toggle()
                            viewModel.toggleVolumeLimiter(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = highlightColor,
                            checkedTrackColor = primaryColor.copy(alpha = 0.6f),
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = Color.Black.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .testTag("switch_volume_limiter")
                            .semantics {
                                stateDescription = if (settingsState.volumeLimiterEnabled) "Enabled" else "Disabled"
                            }
                    )
                }

                // Haptic Feedback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = highlightColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tactile Haptic Feedback",
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Calibrated vibration detents when adjusting faders and sliders",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = settingsState.hapticFeedbackEnabled,
                        onCheckedChange = { isEnabled ->
                            if (isEnabled) {
                                haptics.toggle()
                            }
                            viewModel.toggleHaptic(isEnabled)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = highlightColor,
                            checkedTrackColor = primaryColor.copy(alpha = 0.6f),
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = Color.Black.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .testTag("switch_haptics")
                            .semantics {
                                stateDescription = if (settingsState.hapticFeedbackEnabled) "Enabled" else "Disabled"
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Supported vs Unsupported Features Matrix
        Text(
            text = "HARDWARE CAPABILITY AUDIT",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("capability_matrix_card"),
            surfaceAlpha = 0.65f
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                capabilities.forEach { cap ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    if (cap.isSupported) StatusConnected.copy(alpha = 0.25f) else StatusDisconnected.copy(alpha = 0.25f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (cap.isSupported) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = if (cap.isSupported) "Supported" else "Unsupported",
                                tint = if (cap.isSupported) StatusConnected else StatusDisconnected,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cap.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (cap.isSupported) "SUPPORTED" else "UNAVAILABLE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (cap.isSupported) StatusConnected else StatusDisconnected
                                )
                            }
                            Text(
                                text = cap.description,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Technical details: ${cap.note}",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bluetooth Troubleshooting Guide
        Text(
            text = "BLUETOOTH TROUBLESHOOTING",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        val troubleshootingItems = remember {
            listOf(
                "Speaker doesn't appear in Android Bluetooth settings" to
                        "Make sure the speaker is powered on and enter pairing mode using its manufacturer's instructions. Keep it near the phone, pair it from Android Bluetooth settings, then return here and refresh the device list.",
                "Audio cuts out or crackles at distance" to
                        "Keep the phone and audio device within practical range. Physical barriers, 2.4GHz Wi-Fi crowding, or metal obstructions can cause Bluetooth audio packet loss.",
                "Speaker is connected but there's no sound" to
                        "Check Android's media volume and the speaker's own volume. Confirm that Android selected the expected Bluetooth audio output. This app does not switch physical AUX, USB, or memory-card inputs.",
                "Volume still seems too low" to
                        "Check both Android's media volume and the speaker's own volume controls. Bluetooth volume behavior varies by phone and speaker; this app only sets the Android music-stream volume and its optional in-app limit."
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            troubleshootingItems.forEachIndexed { index, item ->
                val isExpanded = expandedTroubleshootingIndex == index
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    surfaceAlpha = 0.50f,
                    onClick = {
                        haptics.click()
                        expandedTroubleshootingIndex = if (isExpanded) null else index
                    }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.first,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = item.second,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About Studio & Integrity
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            surfaceAlpha = 0.50f
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "BOOMBOX CONTROL STUDIO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = highlightColor
                )
                Text(
                    text = "Version 1.2.0-master • Native Android Kotlin / Jetpack Compose M3",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Uses standard Android Bluetooth routing, system music volume, and optional phone-side equalization when supported. Speaker-specific DSP, battery telemetry, firmware updates, and physical input switching require manufacturer support and are not claimed by this app. The app does not send telemetry to an external server.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(84.dp))
    }
}
