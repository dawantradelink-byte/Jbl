# UI / Reliability Log

Updated: 2026-10-14

## Retry pass

### Fixed
- Bluetooth permission flow now checks the actual Android permission state first. If Bluetooth permissions are already granted, tapping the device refresh action does not trigger another permission dialog.
- Permission callback verifies the resulting permission state instead of treating missing callback entries as granted.
- Bluetooth device discovery no longer inserts a made-up BOOMBOX4MINI device when Bluetooth is disabled, permission is missing, or the paired-device list is empty.
- The speaker connection status now starts as disconnected and the app no longer changes to CONNECTED after a timed delay. Reconnect refreshes the paired-device list and explains that connection/disconnection must be confirmed by Android.

### Existing UI retained
- The ambient background already has a looping animated gradient mesh (about 22 seconds in Full mode, 40 seconds in Gentle mode) and supports Reduced Motion.
- Theme and motion preferences use Android DataStore, so selections are persisted rather than requiring a separate Save button.

### Important limitation / next validation
- This repository snapshot was reviewed through GitHub. I have not run an Android Gradle build or tested the app on the user's phone from this environment.
- Bluetooth audio connection state and hardware DSP/EQ control still require real-device integration and on-device validation. The UI must not claim that hardware is connected or that EQ settings are applied to the speaker unless Android/device APIs confirm it.
- Next check: build/install on a physical Android device, grant Bluetooth permissions once, verify the refresh action does not ask again, and confirm only genuinely paired devices appear.
