package com.example.data

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.SystemClock
import android.view.KeyEvent

class AudioManagerHelper(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    fun getStreamMusicVolume(): Int {
        return try {
            audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 10
        } catch (e: Exception) {
            10
        }
    }

    fun getMaxStreamMusicVolume(): Int {
        return try {
            audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 15
        } catch (e: Exception) {
            15
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

    fun sendMediaKeyEvent(keyCode: Int) {
        try {
            val eventDown = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
            val eventUp = KeyEvent(KeyEvent.ACTION_UP, keyCode)
            audioManager?.dispatchMediaKeyEvent(eventDown)
            audioManager?.dispatchMediaKeyEvent(eventUp)
        } catch (_: Exception) {
        }
    }

    fun togglePlayPause() {
        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
    }

    fun skipNext() {
        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
    }

    fun skipPrevious() {
        sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
    }
}
