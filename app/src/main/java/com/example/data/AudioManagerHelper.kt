package com.example.data

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build

class AudioManagerHelper(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    fun getStreamMusicVolume(): Int {
        return try {
            audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 0
        } catch (_: Exception) {
            0
        }
    }

    fun getMaxStreamMusicVolume(): Int {
        return try {
            audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 1
        } catch (_: Exception) {
            1
        }
    }

    fun setStreamMusicVolume(volumeIndex: Int) {
        try {
            audioManager?.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                volumeIndex.coerceIn(0, getMaxStreamMusicVolume()),
                AudioManager.FLAG_SHOW_UI
            )
        } catch (_: Exception) {
        }
    }

    fun isBluetoothAudioConnected(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val devices = audioManager?.getDevices(AudioManager.GET_DEVICES_OUTPUTS) ?: emptyArray()
                devices.any {
                    it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO
                }
            } else {
                @Suppress("DEPRECATION")
                audioManager?.isBluetoothA2dpOn == true
            }
        } catch (e: Exception) {
            false
        }
    }

}
