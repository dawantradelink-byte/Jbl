package com.example.data

import android.content.Context
import android.media.AudioManager

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

}
