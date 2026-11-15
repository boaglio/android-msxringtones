android-msxringtones
===============

Aplicação para Android - MSX Ringtones

**MSX Ringtones** is an Android app for listening to soundtracks from classic MSX1 and MSX2 games and setting one as your phone's ringtone. *PSG, SCC and FM-PAC for your ears.*

## Games

| MSX1 | MSX2 |
|---|---|
| Antarctic Adventure | Aleste 2 |
| Goonies | Fire Hawk |
| Guardic | Fray |
| King's Valley | Golvellius |
| Knightmare 🔔 | Metal Gear |
| The Maze of Galious | SD Snatcher |
| Gradius 2 | Space Manbow |
| Nemesis 3: The Eve of Destruction 🔔 | Treasure of USAS |
| Pac-Mania | |
| Pippols | |
| Salamander | |
| Zanac | |

🔔 These games have a short jingle that is used when you set them as a notification sound.

## Features

- Play any soundtrack, with a progress bar and time while it plays.
- Set it as your **ringtone, notification sound or alarm**. Badges on each game show which sounds you are using.
- Material 3 design, with Material You colors on Android 12+ and a dark theme.
- Adapts to phones, tablets and foldables, in portrait and landscape.
- Android 13+ themed icon, splash screen and predictive back gesture.

Tap **Set as…** and choose Ringtone, Notification or Alarm. The sound is always saved to your phone. If you allow MSX Ringtones to **modify system settings**, it sets the sound for you. If you'd rather not, choose **Choose myself**, and the app opens Android's Sound settings, where the new sound is already in the list.

## Privacy

The app collects no data and has no Internet permission. See the [privacy policy](PRIVACY.md).

## Requirements

- Android 6.0 (API 23) or newer.

## Screenshots

<p>
  <img src="screenshots/home.png" width="200" alt="Home screen">
  <img src="screenshots/home-dark.png" width="200" alt="Home screen, dark theme">
  <img src="screenshots/msx1.png" width="200" alt="MSX1 games">
  <img src="screenshots/msx2-dark.png" width="200" alt="MSX2 games, dark theme">
  <img src="screenshots/about.png" width="200" alt="About screen">
</p>

On tablets and in landscape, the game list shows more columns:

<img src="screenshots/tablet-msx1.png" width="820" alt="MSX1 games on a tablet">

The screenshots are rendered from the app's real screens by a JVM screenshot test, with no device needed. To regenerate them after a UI change, run `./gradlew :app:recordRoborazziDebug`.

## Building

- **Built with:** Kotlin, Jetpack Compose and Material 3, as a single-activity app.
- **Toolchain:** Android Gradle Plugin 9.5, Gradle 9.9 (wrapper included) and Kotlin 2.4.
- **SDK levels:** compileSdk 37, targetSdk 36, minSdk 23.
- **Needs:** JDK 17 or newer (JDK 21+ to run the tests), and the Android SDK. Set `ANDROID_HOME` or `sdk.dir` in `local.properties`.

```bash
./gradlew :app:assembleDebug        # debug APK: app/build/outputs/apk/debug/app-debug.apk
./gradlew :app:assembleRelease      # release (R8 + resource shrinking; sign with your own key)
./gradlew :app:testDebugUnitTest    # unit + screenshot tests
./gradlew :app:recordRoborazziDebug # regenerate screenshots/ (Roborazzi + Robolectric)
./gradlew :app:lint
```

### Adding a game

1. Put the audio file in `app/src/main/res/raw/<name>.ogg`.
2. Put the title-screen image in `app/src/main/res/drawable-hdpi/`.
3. Add one entry to the `MsxGame` enum in `app/src/main/java/com/olimposystems/android/MsxGame.kt`.

[AGENTS.md](AGENTS.md) has more detail on the project layout and conventions.

## Google Play

https://play.google.com/store/apps/details?id=com.olimposystems.android
