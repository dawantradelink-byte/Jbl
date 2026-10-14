package com.example.data

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

data class DiscoveredSpeaker(
    val name: String,
    val address: String,
    val isPaired: Boolean,
    val isBoomboxTarget: Boolean
)

class BluetoothManagerHelper(private val context: Context) {

    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    fun isBluetoothSupported(): Boolean = bluetoothAdapter != null

    fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

    /** True only when Android reports an active A2DP audio profile connection. */
    fun isA2dpConnected(): Boolean {
        if (!hasBluetoothConnectPermission() || bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            return false
        }
        return try {
            bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP) == BluetoothProfile.STATE_CONNECTED
        } catch (_: SecurityException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun getPairedAudioDevices(): List<DiscoveredSpeaker> {
        if (!hasBluetoothConnectPermission() || bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            return emptyList()
        }

        return try {
            val bonded = bluetoothAdapter.bondedDevices ?: emptySet()
            bonded.map { device ->
                    val name = try {
                        device.name ?: "Unknown Bluetooth Audio"
                    } catch (_: SecurityException) {
                        "Bluetooth Device"
                    }
                    val isTarget = name.contains("BOOMBOX", ignoreCase = true) ||
                            name.contains("B4M", ignoreCase = true)
                    DiscoveredSpeaker(
                        name = name,
                        address = device.address ?: "Address unavailable",
                        isPaired = true,
                        isBoomboxTarget = isTarget
                    )
                }
        } catch (_: SecurityException) {
            emptyList()
        }
    }
}
