# UI / Reliability Log

Updated: 2026-10-14

## Real-functionality pass

### Implemented
- Bluetooth Connect permission is checked before requesting anything; already-granted permission is not requested again. The unused nearby-scan permission was removed because this app only reads paired devices.
- Removed fabricated fallback devices and hard-coded MAC addresses. The list uses Android's real paired-device data.
- Connection state comes from Android's A2DP profile state. The UI does not simulate a connection after a timer.
- Bluetooth Settings opens the real Android Bluetooth settings page. Refresh re-reads paired devices and A2DP status.
- Volume and mute control Android's actual music stream. Initial slider/mute state is read from Android, and unmute restores the last non-zero level.
- Added a phone-side Android system Equalizer integration. Five-band controls are enabled only when the OS provides a usable five-band output effect. The UI labels it unavailable if applying a curve fails. This does not modify speaker-internal DSP.
- Theme, motion, volume limiter, and haptics preferences continue to use Android DataStore.
- Removed fabricated battery percentage, fake track titles and playback progression, simulated PartyBoost linking, simulated input switching, unused fake media transport component, and unverified Bluetooth 5.4 / codec / DSP / surround claims.
- Only Bluetooth is listed as an input capability because that is the only input this integration can verify. Physical AUX/TF/USB mode changes are not simulated.
- Preserved the looping animated background and reduced-motion support.

### Build and test status
- Added GitHub Actions workflow at .github/workflows/android-build.yml to run unit tests, assemble a debug APK, and upload the APK artifact.
- Fixed the CI Android SDK package selection after the first run identified a removed package and then an incorrect API 36.1 package identifier.
- Fixed stale Bluetooth scan references and moved Robolectric tests to API 35 while keeping the app compile SDK at API 36.1.
- Unit tests passed on the latest completed test run. APK assembly then exposed a missing root debug.keystore; the workflow now generates an ephemeral debug key in the runner instead of committing a signing key. The post-fix APK build is still pending and must pass before the app is considered build-verified.
- No physical-device test has been performed from this environment. Real A2DP reporting, phone-side EQ availability, permission behavior, and audible volume/EQ changes still need confirmation on the target phone.

### Intentional limitations
- No speaker-internal DSP, firmware update, battery telemetry, or physical input switching is claimed without a documented vendor protocol/API.
- Media play/pause/skip controls are not implemented. Use the music app's own controls; this app handles Android system volume and supported phone-side EQ.
- The app refreshes paired devices and reads A2DP status; it does not silently force Bluetooth connections.
