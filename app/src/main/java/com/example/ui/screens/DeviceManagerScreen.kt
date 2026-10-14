package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ConnectionStatus
import com.example.model.SpeakerInput
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.CrimsonRosePrimary
import com.example.ui.theme.DreamyPinkHighlight
import com.example.ui.theme.OceanicBlueHighlight
import com.example.ui.theme.PrimaryMint
import com.example.ui.theme.RoastedBrownHighlight
import com.example.ui.theme.SecondarySunsetOrange
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusConnecting
import com.example.ui.theme.StatusDisconnected
import com.example.ui.theme.SunsetOrangeHighlight
import com.example.ui.theme.TertiaryCrimsonRose
import com.example.ui.util.LocalStudioHaptics
import com.example.viewmodel.BoomboxViewModel

@Composable
fun DeviceManagerScreen(
    viewModel: BoomboxViewModel,
    modifier: Modifier = Modifier
) {
    val haptics = LocalStudioHaptics.current
    val context = LocalContext.current
    val speakerState by viewModel.speakerState.collectAsStateWithLifecycle()
    val pairedDevices by viewModel.discoveredDevices.collectAsStateWithLifecycle()
    val highlightColor = MaterialTheme.colorScheme.primary
    val primaryColor = MaterialTheme.colorScheme.primaryContainer

    var showPairingGuide by remember { mutableStateOf(false) }
    var permissionDeniedState by remember { mutableStateOf(false) }

    // Android 12+ Bluetooth runtime permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val connectGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        val scanGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (connectGranted && scanGranted) {
            permissionDeniedState = false
            viewModel.refreshBluetoothDevices()
        } else {
            permissionDeniedState = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("device_manager_screen")
    ) {
        // Screen Header
        Text(
            text = "DEVICE MANAGER",
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "DEVICE STATUS & HARDWARE I/O",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.Bold
            ),
            color = highlightColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Active Connection Status Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("active_device_card"),
            surfaceAlpha = 0.65f
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = speakerState.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Model: ${speakerState.modelNumber} • MAC: ${speakerState.macAddress}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Status Indicator Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (speakerState.connectionStatus) {
                                    ConnectionStatus.CONNECTED -> StatusConnected.copy(alpha = 0.25f)
                                    ConnectionStatus.CONNECTING -> StatusConnecting.copy(alpha = 0.25f)
                                    ConnectionStatus.DISCONNECTED -> StatusDisconnected.copy(alpha = 0.25f)
                                }
                            )
                            .border(
                                1.dp,
                                when (speakerState.connectionStatus) {
                                    ConnectionStatus.CONNECTED -> StatusConnected.copy(alpha = 0.6f)
                                    ConnectionStatus.CONNECTING -> StatusConnecting.copy(alpha = 0.6f)
                                    ConnectionStatus.DISCONNECTED -> StatusDisconnected.copy(alpha = 0.6f)
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = when (speakerState.connectionStatus) {
                                ConnectionStatus.CONNECTED -> "CONNECTED"
                                ConnectionStatus.CONNECTING -> "CONNECTING"
                                ConnectionStatus.DISCONNECTED -> "OFFLINE"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (speakerState.connectionStatus) {
                                ConnectionStatus.CONNECTED -> StatusConnected
                                ConnectionStatus.CONNECTING -> StatusConnecting
                                ConnectionStatus.DISCONNECTED -> StatusDisconnected
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hardware Specs Details Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "CORE PROTOCOL",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = speakerState.bluetoothVersion,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "ACTIVE CODEC",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = speakerState.activeCodec,
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                            color = highlightColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Reconnect / Disconnect (48dp min touch height)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            haptics.snap()
                            viewModel.refreshPairedDevices()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("reconnect_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = highlightColor,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Refresh status", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            haptics.snap()
                            context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("disconnect_button"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (speakerState.connectionStatus == ConnectionStatus.CONNECTED) CrimsonRosePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                listOf(
                                    highlightColor.copy(alpha = 0.4f),
                                    highlightColor.copy(alpha = 0.15f)
                                )
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Bluetooth settings",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hardware Inputs & Physical Ports
        Text(
            text = "HARDWARE INPUT SOURCES",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            speakerState.supportedInputs.forEach { input ->
                val isActive = speakerState.connectionStatus == ConnectionStatus.CONNECTED && speakerState.currentInput == input
                val inputColor = when (input) {
                    SpeakerInput.BLUETOOTH -> PrimaryMint
                    SpeakerInput.AUX -> SecondarySunsetOrange
                    SpeakerInput.TF_CARD -> DreamyPinkHighlight
                    SpeakerInput.USB -> TertiaryCrimsonRose
                }
                GlassCard(
                    modifier = Modifier.testTag("input_${input.name.lowercase()}"),
                    surfaceAlpha = if (isActive) 0.70f else 0.40f,
                    borderAlpha = if (isActive) 0.75f else 0.20f,
                    onClick = {
                        haptics.click()
                        viewModel.selectSpeakerInput(input)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isActive) inputColor.copy(alpha = 0.20f) else Color.Black.copy(alpha = 0.45f)
                                )
                                .border(
                                    1.dp,
                                    if (isActive) inputColor.copy(alpha = 0.80f) else inputColor.copy(alpha = 0.30f),
                                    RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (input) {
                                    SpeakerInput.BLUETOOTH -> Icons.Default.Bluetooth
                                    SpeakerInput.AUX -> Icons.Default.Cable
                                    SpeakerInput.TF_CARD -> Icons.Default.SdCard
                                    SpeakerInput.USB -> Icons.Default.Usb
                                },
                                contentDescription = input.title,
                                tint = if (isActive) inputColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = input.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isActive) inputColor else MaterialTheme.colorScheme.onSurface
                                )
                                if (input.isPhysicalSwitch) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PHYSICAL SWITCH",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                        color = inputColor
                                    )
                                }
                            }
                            Text(
                                text = input.description,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isActive) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active input",
                                tint = inputColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Paired Device Discovery with Permission Check
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PAIRED BLUETOOTH DEVICES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = {
                    haptics.click()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        val required = arrayOf(
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN
                        )
                        val missing = required.filter { permission ->
                            ContextCompat.checkSelfPermission(
                                context,
                                permission
                            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
                        }
                        if (missing.isEmpty()) {
                            permissionDeniedState = false
                            viewModel.refreshBluetoothDevices()
                        } else {
                            permissionLauncher.launch(missing.toTypedArray())
                        }
                    } else {
                        viewModel.refreshBluetoothDevices()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor.copy(alpha = 0.50f),
                    contentColor = highlightColor
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .height(42.dp)
                    .testTag("scan_devices_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh paired Bluetooth devices",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Refresh paired", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        if (permissionDeniedState) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CrimsonRosePrimary.copy(alpha = 0.20f))
                    .border(1.dp, CrimsonRosePrimary.copy(alpha = 0.40f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Bluetooth permission is required to read paired devices and connection status. Open Android Settings > Apps > BOOMBOX Control Studio > Permissions to allow Bluetooth.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    color = Color(0xFFFDA4AF)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Paired devices list
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            pairedDevices.forEach { device ->
                GlassCard(
                    modifier = Modifier.testTag("device_item_${device.address.replace(":", "_")}"),
                    surfaceAlpha = if (device.isBoomboxTarget) 0.60f else 0.35f,
                    borderAlpha = if (device.isBoomboxTarget) 0.50f else 0.20f
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bluetooth,
                            contentDescription = "Bluetooth Device",
                            tint = if (device.isBoomboxTarget) highlightColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = device.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${device.address} • Paired device",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (device.isBoomboxTarget) {
                            GlassBadge(
                                text = "TARGET SPEAKER",
                                textColor = highlightColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Expandable Pairing Guide Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    haptics.click()
                    showPairingGuide = !showPairingGuide
                }
                .testTag("pairing_guide_toggle")
                .semantics {
                    contentDescription = "BOOMBOX4MINI Pairing Instructions, ${if (showPairingGuide) "Expanded" else "Collapsed"}"
                },
            surfaceAlpha = 0.50f
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = highlightColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BOOMBOX4MINI Pairing Instructions",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Icon(
                        imageVector = if (showPairingGuide) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (showPairingGuide) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                AnimatedVisibility(visible = showPairingGuide) {
                    Column(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PairingStepItem("1", "Hold Power button for 2 seconds until acoustic chime sounds.")
                        PairingStepItem("2", "Observe the front LED indicator flashing blue rapidly (Pairing Mode).")
                        PairingStepItem("3", "If paired with another phone, hold Play/Pause button for 3 seconds to unbind.")
                        PairingStepItem("4", "Open Android Settings > Connected Devices > Pair new device.")
                        PairingStepItem("5", "Tap 'BOOMBOX4MINI' to link. Return to this studio for audio management.")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(84.dp))
    }
}

@Composable
private fun PairingStepItem(number: String, text: String) {
    val highlightColor = MaterialTheme.colorScheme.primary

    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.50f))
                .border(1.dp, highlightColor.copy(alpha = 0.40f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = highlightColor
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
