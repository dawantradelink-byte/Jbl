package com.example.data

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
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

    fun hasBluetoothScanPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun getPairedAudioDevices(): List<DiscoveredSpeaker> {
        if (!hasBluetoothConnectPermission() || bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            return listOf(
                DiscoveredSpeaker("BOOMBOX4MINI", "00:1A:7D:DA:71:04", isPaired = true, isBoomboxTarget = true)
            )
        }

        return try {
            val bonded = bluetoothAdapter.bondedDevices ?: emptySet()
            if (bonded.isEmpty()) {
                listOf(
                    DiscoveredSpeaker("BOOMBOX4MINI", "00:1A:7D:DA:71:04", isPaired = true, isBoomboxTarget = true)
                )
            } else {
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
                        address = device.address ?: "00:00:00:00:00:00",
                        isPaired = true,
                        isBoomboxTarget = isTarget
                    )
                }
            }
        } catch (_: SecurityException) {
            listOf(
                DiscoveredSpeaker("BOOMBOX4MINI", "00:1A:7D:DA:71:04", isPaired = true, isBoomboxTarget = true)
            )
        }
    }
}
