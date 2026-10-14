package com.example.model

enum class ConnectionStatus {
    CONNECTED,
    CONNECTING,
    DISCONNECTED
}

enum class SpeakerInput(
    val title: String,
    val description: String,
    val isPhysicalSwitch: Boolean
) {
    BLUETOOTH("Bluetooth 5.4", "Wireless A2DP / AVRCP active audio stream", false),
    AUX("3.5mm AUX Input", "Physical analog line-in detected via 3.5mm jack", true),
    TF_CARD("TF / MicroSD", "Internal MP3/WAV playback from card slot", true),
    USB("USB Flash Drive", "Direct digital playback from USB-A storage", true)
}

data class SpeakerDevice(
    val name: String = "BOOMBOX4MINI",
    val modelNumber: String = "B4M-BT54-GL",
    val macAddress: String = "00:1A:7D:DA:71:04",
    val bluetoothVersion: String = "Bluetooth 5.4 BR/EDR + BLE",
    val connectionStatus: ConnectionStatus = ConnectionStatus.CONNECTED,
    val activeCodec: String = "AAC / SBC HD Audio",
    val ratedPlaybackHours: Int = 5,
    val estimatedMinutesRemaining: Int = 260,
    val isPlayingEstimator: Boolean = false,
    val hasHardwareBatteryTelemetry: Boolean = false,
    val telemetryNotice: String = "Hardware does not report battery telemetry via Bluetooth A2DP. Playback estimate is derived from active streaming time.",
    val supportedInputs: List<SpeakerInput> = listOf(
        SpeakerInput.BLUETOOTH,
        SpeakerInput.AUX,
        SpeakerInput.TF_CARD,
        SpeakerInput.USB
    ),
    val currentInput: SpeakerInput = SpeakerInput.BLUETOOTH,
    val signalDbm: Int = -52
)

data class MediaPlaybackState(
    val isPlaying: Boolean = true,
    val trackTitle: String? = "Midnight Bassline",
    val artistName: String? = "Acoustic Horizon",
    val albumName: String? = "Studio Sessions Vol. 4",
    val positionSeconds: Int = 74,
    val durationSeconds: Int = 236,
    val volumePercent: Int = 68,
    val isMuted: Boolean = false,
    val isMediaSessionActive: Boolean = true
)

enum class EqualizerPreset(val displayName: String, val subtitle: String) {
    BALANCED("Balanced", "Flat studio profile for natural acoustic playback"),
    BASS_BOOST("Bass Boost", "Deep resonant low-end boost for dual radiators"),
    VOCAL("Vocal Clarity", "Elevated mid frequencies for crisp voice and podcasts"),
    CLUB("Club / Electronic", "Dynamic V-shape profile for dance and electronic"),
    CUSTOM("Custom", "User adjusted frequency sliders")
}

data class FrequencyBand(
    val bandIndex: Int,
    val frequencyLabel: String,
    val gainDb: Float // -12f to +12f
)

data class StudioEqualizerState(
    val preset: EqualizerPreset = EqualizerPreset.BALANCED,
    val bands: List<FrequencyBand> = listOf(
        FrequencyBand(0, "60 Hz", 0.0f),
        FrequencyBand(1, "250 Hz", 0.0f),
        FrequencyBand(2, "1 kHz", 0.0f),
        FrequencyBand(3, "4 kHz", 0.0f),
        FrequencyBand(4, "14 kHz", 0.0f)
    ),
    val bassLevel: Float = 0.5f, // 0..1 master bass
    val trebleLevel: Float = 0.5f, // 0..1 master treble
    val bassBoostAmount: Float = 0.40f, // 0..1
    val virtualizerAmount: Float = 0.25f, // 0..1
    val isDspSimulationNoteVisible: Boolean = true
)
