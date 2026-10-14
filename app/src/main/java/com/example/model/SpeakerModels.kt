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
    BLUETOOTH("Bluetooth audio", "Android A2DP audio connection status", false),
    AUX("3.5mm AUX Input", "Physical analog line-in detected via 3.5mm jack", true),
    TF_CARD("TF / MicroSD", "Internal MP3/WAV playback from card slot", true),
    USB("USB Flash Drive", "Direct digital playback from USB-A storage", true)
}

data class SpeakerDevice(
    val name: String = "No Bluetooth audio connected",
    val modelNumber: String = "Not available",
    val macAddress: String = "Not connected",
    val bluetoothVersion: String = "Unavailable",
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val activeCodec: String = "Unavailable",
    val supportedInputs: List<SpeakerInput> = listOf(SpeakerInput.BLUETOOTH),
    val currentInput: SpeakerInput = SpeakerInput.BLUETOOTH,
)

data class AudioOutputState(
    val volumePercent: Int = 0,
    val isMuted: Boolean = false
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
)
