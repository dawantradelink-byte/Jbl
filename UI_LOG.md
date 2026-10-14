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

### Implemented and merged to `main`
- Fixed the theme application bug: the selected skin colours were animated but were not used by the Material color scheme. The selected accent and its surface tint now flow through Material controls, glass cards, the bottom navigation, volume controls, the speaker artwork, and the animated background. Skin switches remain saved through DataStore.
- Reworked the Home status header into two clear, compact live status chips. Removed the oversized redundant Bluetooth label and non-functional profile-avatar decoration to prevent horizontal crowding.
- Replaced the speaker illustration's play-triangle look with an equalizer mark, so artwork no longer suggests that tapping it starts playback.
- Replaced the former media-placeholder panel with a classic moving 32-bar EQ visualizer and a response curve derived from the effective five-band EQ gains (including bass/treble offsets). Labels explicitly avoid claiming live audio FFT/microphone capture.
- The narrow vertical control at the right edge of the screenshot is not drawn by the app's Compose screens; it is consistent with Android's temporary system volume overlay. The app's in-app master volume slider and +/-5 controls remain the actual interactive volume controls.
- Preserved the looping background motion and reduced-motion preference.

### Validation
- Pull-request CI and APK build passed in [run 38537364248](https://github.com/dawantradelink-byte/Jbl/actions/runs/38537364248) on tested merge candidate `64ca2d6bb7e419c8bf6c9e9d76c4637a7aae0e6e`: unit tests, debug APK assembly, and artifact upload succeeded.
- No physical-phone interaction test is claimed from this environment.


### Verified updated debug APK
- CI artifact ID: `11881286303`; artifact name `boombox-control-studio-debug`.
- CI ZIP SHA-256: `23449320b5146a7f671627f196fe14dbd6f90154d5294e96109951173b0dc3f8`.
- Extracted APK SHA-256: `ee8b7fa43fb3230e829739370efea3c94b53b842f56b8c8f2341277467aae405`.
- Downloaded APK and ZIP archives were checked with `unzip -t`; no archive-integrity errors were reported.
- This is a CI debug APK, not a release/Play Store build. CI currently generates an ephemeral debug signing key; an existing APK signed by a different debug key may need to be uninstalled before this build can be installed.
- Physical-phone interaction and visual regression testing have not been performed from this environment.


## Sound-reactive EQ styles — 2026-10-14

### Implemented and merged to `main`
- Redesigned the blue-marked speaker hero with a compact, clearly non-interactive EQ emblem; added a swipeable six-style audio visualizer below it.
- Visualizer styles: Spectrum Bars, Neon Wave, Circular Pulse, Mirror Peaks, Retro Scope, and Aurora Flow. Horizontal swipe cycles between styles and six position dots show the current style.
- Added an opt-in `LIVE SOUND` control. It requests Android `RECORD_AUDIO` permission only when tapped, then attempts Android output-mix FFT capture via `android.media.audiofx.Visualizer` session 0. When the OEM/OS allows it, bar/wave levels follow captured FFT. If the device blocks global capture, the UI reports that and retains the EQ-shaped preview instead of pretending to be live.
- The equalizer response curve remains tied to the selected five-band EQ profile; the sound-reactive graphics are a separate signal layer.
- Added cleanup for the Android Visualizer effect when the live mode is turned off or the screen is disposed.

### Validation
- Main-branch CI run [38546591771](https://github.com/dawantradelink-byte/Jbl/actions/runs/38546591771) passed unit tests, debug APK assembly, and artifact upload for commit `b2a1564ff46e3023a7a5945e7d18991bde04c9fb`.
- APK artifact ID `11885320235`; ZIP SHA-256 `6358f7d637e0cd9f876236f33895d02d736b9e01150a457c58672fb5a6c9abda`; extracted APK SHA-256 `3d990bafc097bfcadb610294b12dbc4563a6566c51b0276ea160bb171db9aaf3`.
- APK and artifact ZIP passed archive integrity checks. No physical-phone test or Android 16/17 emulator/device run was available here; output-mix capture remains OEM/OS-policy dependent.


## Speaker hero animation redesign — 2026-10-14
- Added five selectable animated effects directly inside the blue-marked speaker stage: Sonic Rings, Orbit particles, Funky Bars, Neon Scan, and Aurora ribbons.
- Swipe left/right on the speaker illustration to change effects. The active style is marked in blue; a compact label row explains the gesture.
- The motion is Compose/Canvas animation and does not depend on a Motorola-only API. The visual effects are intended to run across supported Android versions, including Android 16 and 17; actual rendering/performance still needs device verification.
- Live sound FFT remains a separate optional visualizer capability. The speaker hero animations are ambient effects, not a claim that audio is currently playing.


## Speaker hero animation redesign — 2026-10-14

- Added six swipe-selectable animation modes to the blue-marked speaker hero: Sonic Pulse, Orbit, Funky Spectrum, Neon Scanner, Aurora Flow, and Starburst.
- Added electric-blue/neon-blue as signature accents with expanding rings, orbiting particles, moving spectrum bars, a scanner beam, layered ribbons, and radial sparkles.
- Increased the gesture cycle from five to six styles and updated the on-screen labels/instruction.
- Effects are Canvas-based Compose animations and do not require a Motorola-specific API. They are intended to run on Android 16/17 as well as other supported Android versions, subject to normal device rendering performance.
- These hero effects are ambient animation, not claims of live sound capture. Live output FFT remains a separate, opt-in visualizer feature.
- The main-branch commit containing the six hero animations passed CI in run [38546591771](https://github.com/dawantradelink-byte/Jbl/actions/runs/38546591771). No physical device test is claimed.


### Audio visualizer final verification — 2026-10-14
- Latest merged-main CI run [38546591771](https://github.com/dawantradelink-byte/Jbl/actions/runs/38546591771) passed: unit tests, APK assembly, and artifact upload.
- CI artifact ID `11885320235` (`boombox-control-studio-debug`); ZIP SHA-256 `6358f7d637e0cd9f876236f33895d02d736b9e01150a457c58672fb5a6c9abda`.
- Extracted APK SHA-256 `3d990bafc097bfcadb610294b12dbc4563a6566c51b0276ea160bb171db9aaf3`; ZIP and APK archive integrity checks passed.
- No physical-device or Android 17 emulator test has been performed from this environment. Actual sound-capture availability depends on OS/OEM audio-capture policy.


## Master volume knob and output modes — 2026-10-14

### Implemented on `fix/smooth-volume-knob-max-output` (CI pending)
- Reworked the volume control into a larger circular knob with a spring-smoothed animated position and theme-colored gradient.
- Fixed touch handling: tap and drag are handled across the full track, not only within the tiny thumb. The previous thumb-local drag coordinates could jump the volume unexpectedly.
- Added explicit `MAX OUTPUT` mode. On user tap it disables the app's 85% limiter and requests 100% Android music-stream volume. This is a system volume request, not an audio amplifier or speaker DSP boost.
- Added an `85% SAFE LIMIT` mode that re-enables the limiter and caps the app's volume control at 85%.
- Max output can be loud; recommend lowering volume if audio is uncomfortable. No claim of sound above Android's hardware/system maximum.
- Automated CI unit tests and APK build pending. Physical speaker loudness / sound-bar testing cannot be performed in this remote environment.
