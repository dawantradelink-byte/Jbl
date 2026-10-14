# UI / Reliability Log

Updated: 2026-10-14

## Reliability pass

### Real behavior implemented
- Bluetooth permissions are checked before requesting them. Already-granted permissions are not requested again.
- Device list shows real Android paired-device data; fabricated fallback BOOMBOX4MINI entries were removed.
- Connection status reads Android's A2DP profile state instead of being set to CONNECTED after a timer.
- Bluetooth settings button opens the actual Android Bluetooth settings screen. Refresh status re-reads paired devices and the A2DP profile state.
- Volume and mute controls change Android's actual music stream volume. Unmute restores the last non-zero volume level.
- Equalizer presets and sliders call Android's system output Equalizer effect. The UI checks whether the effect is supported; controls are disabled and the UI reports UNAVAILABLE if the platform cannot apply it. This affects phone-side audio, not speaker-internal DSP.
- Theme, motion, volume limiter, and haptics settings remain persisted with Android DataStore.
- Removed fabricated battery percentage, fake media tracks/playback progression, fake PartyBoost multi-link state, and simulated speaker-input switching.
- Kept the existing looping ambient gradient background and reduced-motion support.

### CI / validation
- Added .github/workflows/android-build.yml to run unit tests and assemble a debug APK, then upload the APK as a workflow artifact.
- Initial CI attempt exposed an Android SDK setup issue caused by requesting the obsolete tools package; workflow updated to install only platform-tools through the action and install platform/build packages with sdkmanager.
- Current CI run: pending/in progress. Do not treat build/tests as passed until the GitHub Actions run completes successfully.
- No physical-device test has been performed from this environment. A2DP status, Android output EQ support, permission flow, and actual audio behavior still need verification on the target phone.

### Known limits, intentionally not faked
- The app does not claim to control the speaker's internal DSP or physical AUX/TF/USB mode. Those features require a documented speaker-specific protocol/API or physical controls.
- Media transport buttons were removed from the home UI until a real Android media-session integration is implemented. Use the music app's own play/pause/skip controls.
- The app refreshes paired devices and reads A2DP connection state; it does not silently force a Bluetooth connection. Pair/connect/disconnect is handled by Android Bluetooth settings.
