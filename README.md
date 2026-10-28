# Rail Architect

Rail Architect is an early native Android railway-building game prototype written in Java with a custom Canvas renderer.

## Current prototype

- Landscape, full-screen game view
- Grid-based tile placement: erase, straight, vertical, curve, switch, station and signal
- A simple train movement simulation
- Run, pause, reset and clear controls
- GitHub Actions builds a debug APK automatically

This is an early prototype. Full track connectivity, switch routing, save/load, multiple trains, timetables and collision prevention are not implemented yet.

## Build an APK with GitHub Actions

The workflow runs automatically when code is pushed to `main` or `master`, on pull requests, and can also be started manually.

1. Open the repository's **Actions** tab.
2. Select **Build Rail Architect APK**.
3. Open the latest completed run.
4. Download **RailArchitect-debug-apk** from the Artifacts section.
5. Extract the downloaded ZIP to get `app-debug.apk`.

No signing secrets are needed for a debug APK. It is signed with Android's debug key and is intended for testing, not Play Store release.

## Build locally

Requirements: JDK 17, Android SDK platform 35, Android build tools 35.0.0 and Gradle 8.9.

    gradle --no-daemon assembleDebug

APK output: `app/build/outputs/apk/debug/app-debug.apk`
