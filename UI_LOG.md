# UI / Reliability Log

Updated: 2026-10-14

## Real-functionality pass

### Implemented
- Bluetooth Connect permission is checked before requesting anything; already-granted permission is not requested again. The unused nearby-scan permission was removed because the app only reads paired devices.
- Removed fabricated fallback devices and hard-coded MAC addresses. The list uses Android's real paired-device data and no longer substitutes placeholder MAC values.
- Connection state comes from Android's A2DP profile state. The UI does not simulate a connection after a timer.
- Bluetooth Settings opens the real Android Bluetooth settings page. Refresh re-reads paired devices and A2DP status.
- Volume and mute control Android's actual music stream. Initial slider/mute state is read from Android, unmute restores the last non-zero level, and the app volume limiter is reapplied when restoring audio.
- Added a phone-side Android system Equalizer integration. Five-band controls and presets are enabled only when the OS provides a usable output effect. This does not modify speaker-internal DSP.
- Theme, motion, volume limiter, and haptics preferences continue to use Android DataStore.
- Removed fabricated battery percentage, fake track titles and playback progression, simulated PartyBoost linking, simulated input switching, unused fake media transport component, and unverified Bluetooth/codec/DSP/surround claims.
- Only Bluetooth is listed as an input capability because that is the only input this integration can verify. Physical AUX/TF/USB mode changes are not simulated.
- Preserved the looping animated background and reduced-motion support.
- Replaced model-specific pairing/troubleshooting instructions with generic Android Bluetooth steps; the UI no longer assumes unverified button sequences, LED patterns, or speaker input-switch behavior.

### Build and test status
- Latest verified app-code build: GitHub Actions run https://github.com/dawantradelink-byte/Jbl/actions/runs/38524808552 (commit `8460d6151eadc331368ce1d1d075269834bcb1d5`); unit tests, debug APK assembly, and artifact upload all passed.
- Uploaded artifact `boombox-control-studio-debug` (artifact ID 11875384519; 22,087,839-byte ZIP). The APK was extracted and both ZIP container and APK archive integrity were checked locally. APK SHA-256: `812e51eff085b2f364dd18b30ff63c8652bbcfac5888bd2631c98326e67ad897`.
- Android CI generates an ephemeral debug keystore in the runner so no signing key is committed to the repository.
- Pull request #1 was merged to `main` after the successful CI run.
- No physical-device test has been performed from this environment. Real A2DP reporting, phone-side EQ availability, permission behavior, and audible volume/EQ changes still need confirmation on a target phone.

### Intentional limitations
- No speaker-internal DSP, firmware update, battery telemetry, or physical input switching is claimed without a documented vendor protocol/API.
- Media play/pause/skip controls are not implemented. Use the music app's own controls; this app handles Android system volume and supported phone-side EQ.
- The app refreshes paired devices and reads A2DP status; it does not silently force Bluetooth connections.


## Theme and EQ visualizer pass — 2026-10-14

### Implemented on `fix/theme-eq-visualizer` (CI verification pending)
- Fixed the theme application bug: the selected skin colours were animated but were not used by the Material color scheme. The selected accent and its surface tint now flow through Material controls, glass cards, the bottom navigation, volume controls, the speaker artwork, and the animated background. Skin switches remain saved through DataStore.
- Reworked the Home status header into two clear, compact live status chips. Removed the oversized redundant Bluetooth label and non-functional profile-avatar decoration to prevent horizontal crowding.
- Replaced the speaker illustration's play-triangle look with an equalizer mark, so artwork no longer suggests that tapping it starts playback.
- Replaced the former media-placeholder panel with a classic moving 32-bar EQ visualizer and a response curve derived from the effective five-band EQ gains (including bass/treble offsets). Labels explicitly avoid claiming live audio FFT/microphone capture.
- The narrow vertical control at the right edge of the screenshot is not drawn by the app's Compose screens; it is consistent with Android's temporary system volume overlay. The app's in-app master volume slider and +/-5 controls remain the actual interactive volume controls.
- Preserved the looping background motion and reduced-motion preference.

### Validation
- Pull-request CI and APK build: pending.
- No physical-phone interaction test is claimed from this environment.
