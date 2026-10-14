package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AudioManagerHelper
import com.example.data.BluetoothManagerHelper
import com.example.data.DiscoveredSpeaker
import com.example.data.StudioPreferencesRepository
import com.example.data.SystemEqualizerHelper
import com.example.model.ConnectionStatus
import com.example.model.EqualizerPreset
import com.example.model.FrequencyBand
import com.example.model.AudioOutputState
import com.example.model.SpeakerDevice
import com.example.model.SpeakerInput
import com.example.model.StudioEqualizerState
import com.example.ui.theme.MotionPreference
import com.example.ui.theme.StudioTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsState(
    val volumeLimiterEnabled: Boolean = true,
    val maxSafeVolumePercent: Int = 85,
    val hapticFeedbackEnabled: Boolean = true
)

class BoomboxViewModel(application: Application) : AndroidViewModel(application) {

    private val audioHelper = AudioManagerHelper(application)
    private val btHelper = BluetoothManagerHelper(application)
    private val prefsRepo = StudioPreferencesRepository(application)
    private val systemEqualizer = SystemEqualizerHelper()
    private val _audioEffectAvailable = MutableStateFlow(systemEqualizer.isAvailable)
    val audioEffectAvailable: StateFlow<Boolean> = _audioEffectAvailable.asStateFlow()

    val activeTheme: StateFlow<StudioTheme> = prefsRepo.activeThemeFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, StudioTheme.SUNSET_GLOW)

    val motionPreference: StateFlow<MotionPreference> = prefsRepo.motionPreferenceFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, MotionPreference.FULL)

    private val _speakerState = MutableStateFlow(
        SpeakerDevice(connectionStatus = ConnectionStatus.DISCONNECTED, macAddress = "Not connected")
    )
    val speakerState: StateFlow<SpeakerDevice> = _speakerState.asStateFlow()

    private val _audioOutputState = MutableStateFlow(createInitialAudioOutputState())
    val audioOutputState: StateFlow<AudioOutputState> = _audioOutputState.asStateFlow()
    private var lastNonMutedVolumePercent: Int = _audioOutputState.value.volumePercent.coerceAtLeast(1)

    private val _equalizerState = MutableStateFlow(StudioEqualizerState())
    val equalizerState: StateFlow<StudioEqualizerState> = _equalizerState.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredSpeaker>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredSpeaker>> = _discoveredDevices.asStateFlow()

    private val _userFeedbackMessage = MutableStateFlow<String?>(null)
    val userFeedbackMessage: StateFlow<String?> = _userFeedbackMessage.asStateFlow()

    init {
        refreshPairedDevices()
        observeStoredSettings()
        applyCurrentEqualizer()
    }

    private fun observeStoredSettings() {
        viewModelScope.launch {
            prefsRepo.volumeLimiterFlow.collect { limiter ->
                _settingsState.update { it.copy(volumeLimiterEnabled = limiter) }
            }
        }
        viewModelScope.launch {
            prefsRepo.hapticsEnabledFlow.collect { haptics ->
                _settingsState.update { it.copy(hapticFeedbackEnabled = haptics) }
            }
        }
    }

    private fun createInitialAudioOutputState(): AudioOutputState {
        val percent = calculateInitialVolumePercent()
        return AudioOutputState(volumePercent = percent, isMuted = percent == 0)
    }

    private fun calculateInitialVolumePercent(): Int {
        val current = audioHelper.getStreamMusicVolume()
        val max = audioHelper.getMaxStreamMusicVolume().coerceAtLeast(1)
        return ((current.toFloat() / max.toFloat()) * 100).toInt().coerceIn(0, 100)
    }

    fun setVolume(newPercent: Int) {
        val limitedPercent = if (_settingsState.value.volumeLimiterEnabled) {
            newPercent.coerceIn(0, _settingsState.value.maxSafeVolumePercent)
        } else {
            newPercent.coerceIn(0, 100)
        }

        if (limitedPercent > 0) lastNonMutedVolumePercent = limitedPercent
        _audioOutputState.update { it.copy(volumePercent = limitedPercent, isMuted = limitedPercent == 0) }

        // Sync with Android system STREAM_MUSIC
        val maxSteps = audioHelper.getMaxStreamMusicVolume().coerceAtLeast(1)
        val targetStep = ((limitedPercent / 100f) * maxSteps).toInt()
        audioHelper.setStreamMusicVolume(targetStep)
    }

    fun toggleMute() {
        val current = _audioOutputState.value
        val nextVolume = if (current.isMuted) lastNonMutedVolumePercent.coerceIn(1, 100) else {
            lastNonMutedVolumePercent = current.volumePercent.coerceAtLeast(1)
            0
        }
        val maxSteps = audioHelper.getMaxStreamMusicVolume().coerceAtLeast(1)
        val targetStep = ((nextVolume / 100f) * maxSteps).toInt().coerceIn(0, maxSteps)
        audioHelper.setStreamMusicVolume(targetStep)
        _audioOutputState.update { it.copy(isMuted = nextVolume == 0, volumePercent = nextVolume) }
    }

    // Connection state is refreshed from Android's real A2DP profile state in refreshPairedDevices().
    fun selectSpeakerInput(input: SpeakerInput) {
        val message = if (input == SpeakerInput.BLUETOOTH) {
            "Bluetooth connection is managed by Android. Use the Bluetooth settings button to connect or disconnect."
        } else {
            "${input.title} cannot be controlled from this app. Use the speaker's physical mode button."
        }
        showToastMessage(message)
    }

    fun refreshPairedDevices() {
        _discoveredDevices.value = btHelper.getPairedAudioDevices()
        val connected = btHelper.isA2dpConnected()
        _speakerState.update { current ->
            if (connected) {
                current.copy(
                    name = "Bluetooth audio connected",
                    modelNumber = "Android-reported A2DP",
                    macAddress = "Not exposed by this status check",
                    bluetoothVersion = "A2DP connection",
                    connectionStatus = ConnectionStatus.CONNECTED,
                    activeCodec = "System managed"
                )
            } else {
                current.copy(
                    name = "No Bluetooth audio connected",
                    modelNumber = "Not available",
                    macAddress = "Not connected",
                    bluetoothVersion = "Unavailable",
                    connectionStatus = ConnectionStatus.DISCONNECTED,
                    activeCodec = "Unavailable"
                )
            }
        }
    }

    fun refreshBluetoothDevices() {
        refreshPairedDevices()
        showToastMessage("Paired Bluetooth devices and A2DP status refreshed.")
    }

    // Equalizer Controls
    fun selectPreset(preset: EqualizerPreset) {
        val newBands = when (preset) {
            EqualizerPreset.BALANCED -> listOf(
                FrequencyBand(0, "60 Hz", 0.0f),
                FrequencyBand(1, "250 Hz", 0.0f),
                FrequencyBand(2, "1 kHz", 0.0f),
                FrequencyBand(3, "4 kHz", 0.0f),
                FrequencyBand(4, "14 kHz", 0.0f)
            )
            EqualizerPreset.BASS_BOOST -> listOf(
                FrequencyBand(0, "60 Hz", 7.5f),
                FrequencyBand(1, "250 Hz", 4.0f),
                FrequencyBand(2, "1 kHz", 0.0f),
                FrequencyBand(3, "4 kHz", 1.0f),
                FrequencyBand(4, "14 kHz", 2.0f)
            )
            EqualizerPreset.VOCAL -> listOf(
                FrequencyBand(0, "60 Hz", -2.0f),
                FrequencyBand(1, "250 Hz", 0.5f),
                FrequencyBand(2, "1 kHz", 4.5f),
                FrequencyBand(3, "4 kHz", 5.0f),
                FrequencyBand(4, "14 kHz", 2.5f)
            )
            EqualizerPreset.CLUB -> listOf(
                FrequencyBand(0, "60 Hz", 6.0f),
                FrequencyBand(1, "250 Hz", 2.5f),
                FrequencyBand(2, "1 kHz", -1.5f),
                FrequencyBand(3, "4 kHz", 3.0f),
                FrequencyBand(4, "14 kHz", 5.5f)
            )
            EqualizerPreset.CUSTOM -> _equalizerState.value.bands
        }

        val bass = when (preset) {
            EqualizerPreset.BASS_BOOST -> 0.85f
            EqualizerPreset.CLUB -> 0.70f
            EqualizerPreset.BALANCED -> 0.50f
            EqualizerPreset.VOCAL -> 0.35f
            EqualizerPreset.CUSTOM -> _equalizerState.value.bassLevel
        }

        val treble = when (preset) {
            EqualizerPreset.VOCAL -> 0.75f
            EqualizerPreset.CLUB -> 0.65f
            EqualizerPreset.BALANCED -> 0.50f
            EqualizerPreset.BASS_BOOST -> 0.45f
            EqualizerPreset.CUSTOM -> _equalizerState.value.trebleLevel
        }

        _equalizerState.update {
            it.copy(
                preset = preset,
                bands = newBands,
                bassLevel = bass,
                trebleLevel = treble
            )
        }
        applyCurrentEqualizer()
    }

    fun updateBandGain(bandIndex: Int, newGain: Float) {
        val updated = _equalizerState.value.bands.map {
            if (it.bandIndex == bandIndex) it.copy(gainDb = newGain.coerceIn(-12f, 12f)) else it
        }
        _equalizerState.update {
            it.copy(
                preset = EqualizerPreset.CUSTOM,
                bands = updated
            )
        }
        applyCurrentEqualizer()
    }

    fun updateBassLevel(level: Float) {
        _equalizerState.update {
            it.copy(
                preset = EqualizerPreset.CUSTOM,
                bassLevel = level.coerceIn(0f, 1f)
            )
        }
        applyCurrentEqualizer()
    }

    fun updateTrebleLevel(level: Float) {
        _equalizerState.update {
            it.copy(
                preset = EqualizerPreset.CUSTOM,
                trebleLevel = level.coerceIn(0f, 1f)
            )
        }
        applyCurrentEqualizer()
    }

    fun resetEqualizerFlat() {
        selectPreset(EqualizerPreset.BALANCED)
        showToastMessage(
            if (_audioEffectAvailable.value) "System output equalizer reset to flat."
            else "Equalizer is unavailable on this device; no audio changes were applied."
        )
    }

    private fun applyCurrentEqualizer() {
        val state = _equalizerState.value
        val bassOffset = (state.bassLevel - 0.5f) * 12f
        val trebleOffset = (state.trebleLevel - 0.5f) * 12f
        val gains = state.bands.mapIndexed { index, band ->
            val toneOffset = when (index) {
                0, 1 -> bassOffset
                3, 4 -> trebleOffset
                else -> 0f
            }
            (band.gainDb + toneOffset).coerceIn(-12f, 12f)
        }
        _audioEffectAvailable.value = systemEqualizer.applyFiveBandCurve(gains)
    }

    override fun onCleared() {
        systemEqualizer.release()
        super.onCleared()
    }

    // Settings & Theme
    fun selectTheme(theme: StudioTheme) {
        viewModelScope.launch {
            prefsRepo.setTheme(theme)
            showToastMessage("Theme applied: ${theme.title}")
        }
    }

    fun selectMotionPreference(preference: MotionPreference) {
        viewModelScope.launch {
            prefsRepo.setMotionPreference(preference)
            showToastMessage("Motion: ${preference.title}")
        }
    }

    fun toggleVolumeLimiter(enabled: Boolean) {
        // Apply immediately so the next slider event cannot exceed the newly selected limit.
        _settingsState.update { it.copy(volumeLimiterEnabled = enabled) }
        if (enabled && _audioOutputState.value.volumePercent > _settingsState.value.maxSafeVolumePercent) {
            setVolume(_settingsState.value.maxSafeVolumePercent)
        }
        viewModelScope.launch {
            prefsRepo.setVolumeLimiter(enabled)
        }
    }

    fun toggleHaptic(enabled: Boolean) {
        _settingsState.update { it.copy(hapticFeedbackEnabled = enabled) }
        viewModelScope.launch {
            prefsRepo.setHapticsEnabled(enabled)
        }
    }

    fun clearFeedbackMessage() {
        _userFeedbackMessage.value = null
    }

    private fun showToastMessage(msg: String) {
        _userFeedbackMessage.value = msg
    }
}
