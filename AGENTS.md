# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project overview

**MSX Ringtones** is a small Android app, published on Google Play as `com.olimposystems.android`. Users can listen to soundtracks from classic MSX1 and MSX2 games and set one as the phone's default ringtone.

- Language: **Kotlin**. UI is **Jetpack Compose** with **Material 3**.
- The app has a single activity, `MSXringtonesActivity`. Screens are composables, and navigation is a simple screen stack in `ui/MsxApp.kt`, so there is no Navigation library.
- Single Gradle module: `:app`.
- Tests: `app/src/test/` (JUnit 4, JVM-only). Run them with `./gradlew :app:testDebugUnitTest`. `ScreenshotTest` renders the real screens with Robolectric and Roborazzi, and the first run downloads a roughly 200 MB Robolectric Android runtime.
- After any UI change, run `./gradlew :app:recordRoborazziDebug`. It rewrites the README images in `screenshots/` (home, home-dark, msx1, msx2-dark, about, tablet-msx1), and the regenerated images must be committed with the change.
- Code identifiers, log messages and comments are mostly in **Portuguese**. For example: `nome` means "name", `arquivoId` means "file id", `maquina` means "machine", `tela` means "screen", `sobre` means "about", `voltar` means "back", `tocando` means "playing", and `define` means "set". User-facing strings are in English only, in `res/values/strings.xml`. Keep that split, and don't add translations.

## Build

| Item | Value |
|---|---|
| Android Gradle Plugin | 9.5.1, with AGP's built-in Kotlin support. The `org.jetbrains.kotlin.android` plugin is **not** applied |
| Kotlin | 2.4.21. It is declared `apply false` in the root `build.gradle` to upgrade AGP's bundled version. The Compose compiler plugin must use the same version |
| Gradle | 9.9.1 via the committed wrapper (`./gradlew`) |
| compileSdk | 37 (current AndroidX/Compose libraries require it) |
| targetSdk | 36 (Google Play requires 36 for updates since Aug 2026). Raising it opts into Android 17 behavior changes, so test before you bump it |
| minSdk | 23 |
| Java | source/target 17; run Gradle with JDK 17+, but the screenshot tests (Robolectric, SDK 36) need JDK 21+ |
| Dependencies | Compose BOM, material3, activity-compose, core-ktx, core-splashscreen, lifecycle-runtime-compose (`app/build.gradle`) |
| versionCode / versionName | in `app/build.gradle` (currently 12 / 2.0). The About screen reads `BuildConfig.VERSION_NAME` |

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease   # R8 + resource shrinking on; unsigned unless the ANDROID_KEYSTORE_* env vars are set
tools/android-release.sh         # signed Play bundle → release/MSXRingtones-<version>-<code>.aab (needs release.env)
./gradlew :app:lint
```

- `local.properties` is git-ignored (machine-specific `sdk.dir`). Create it or set `ANDROID_HOME`.
- Release signing: `app/build.gradle` signs the release only when `ANDROID_KEYSTORE_FILE`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS` and `ANDROID_KEY_PASSWORD` are set. `tools/android-release.sh` loads them from `release.env` (copy `release.env.example`), runs the unit tests and lint, builds the bundle, and checks that it's signed with that key. If `ANDROID_EXPECTED_SHA1` is set, it refuses any other key. A key weaker than 2048-bit RSA only warns, because the app's original 1.x key is 1024-bit and Play still accepts it for updates. `release.env`, `*.jks`, `*.keystore` and `release/` are git-ignored. Never commit them, and never print or log their contents.
- CI: `.github/workflows/ci.yml` runs on every push to `main` and on every PR. It runs `verifyRoborazziDebug` (unit tests plus screenshot comparison, with a 1% tolerance), lint, and the debug, release and bundle builds, and it uploads the debug APK as an artifact. If you change the UI, re-record the screenshots, or CI will fail.
- When you upgrade Kotlin, change both plugin versions in the root `build.gradle` together.

## Source layout

```
app/src/main/java/com/olimposystems/android/
├── MSXringtonesActivity.kt   # Only activity: splash screen, edge-to-edge, setContent { MsxTheme { MsxApp() } }.
│                             # The class name is kept from 1.x so existing launcher shortcuts keep working.
├── MsxGame.kt                # enum Maquina (MSX1/MSX2) + enum MsxGame (name, machine, R.raw, R.drawable,
│                             # optional short notification R.raw). audioPara(tipo) picks the file to save
├── audio/RingtonePlayer.kt   # One MediaPlayer at a time, with audio focus. `tocando` is Compose state
├── util/TipoToque.kt         # enum TOQUE / NOTIFICACAO / ALARME: RingtoneManager type, folder, MediaStore flag, labels
├── util/RingtoneSetter.kt    # suspend define(game, tipo): saves .ogg to Ringtones/, Notifications/ or Alarms/
│                             # (MediaStore on API 29+, file path on 23-28). Sets it as default only if
│                             # WRITE_SETTINGS is granted. atuais() returns which games are set now (badges)
└── ui/
    ├── MsxApp.kt             # Screen stack (Tela enum), BackHandler, shared RingtonePlayer
    ├── HomeScreen.kt         # MSX1 / MSX2 cards
    ├── GameListScreen.kt     # Adaptive grid of game cards + permission flow (dialog, launcher, snackbar)
    ├── AboutScreen.kt        # Title, logo, privacy-policy link (PRIVACY.md on GitHub), version
    ├── Comum.kt              # BarraSuperior (top app bar), PaddingValues.mais()
    └── theme/Theme.kt        # MsxTheme: dynamic color on 12+, MSX-blue fallback, dark theme
app/src/main/res/
├── raw/*.ogg                 # The ringtones
├── drawable-hdpi/            # Game title screens, msx1_default / msx2_default logos, olimposystems
├── drawable/                 # Vector icons (ic_play, ic_stop, ...) + ic_launcher_foreground / _monochrome
├── mipmap-anydpi-v26/ic_launcher.xml   # Adaptive icon: MSX-blue background, pixel-art ♫ foreground, monochrome
├── mipmap-*dpi/ic_launcher.png         # Legacy icon PNGs for API 23-25
└── values/, values-night/    # strings, colors, Theme.MSX / Theme.MSX.Starting (splash)
```

### Runtime flow

1. `MsxApp` holds a saveable stack of `Tela` values. Back and predictive-back gestures pop it through `BackHandler`. Changing screens or `ON_STOP` stops playback.
2. `GameListScreen` shows `MsxGame.da(maquina)` in a `LazyVerticalGrid` with adaptive columns, so it shows one column on phones and more on tablets and foldables.
   - The play/stop toggle calls `player.alterna(game)`.
   - The card shows a progress bar and time while its track plays (`player.progresso()`, polled every 250 ms).
   - **Set as…** opens a menu (Ringtone / Notification / Alarm). It calls `RingtoneSetter.define(context, game, tipo)` on `Dispatchers.IO`, and the result is handled like this:
     - `Ok`: the sound was saved and set, so show a snackbar.
     - `Salvo(uri)`: the sound was saved, but the app doesn't have "Modify system settings", which is **optional**. Show a dialog with two choices:
       - **Allow & set** opens `ACTION_MANAGE_WRITE_SETTINGS`. On `ON_RESUME`, if the permission was granted, `aplica()` sets the saved sound.
       - **Choose myself** opens `ACTION_SOUND_SETTINGS`, where the user picks the sound.
     - `PrecisaPermissaoArmazenamento` (API 23-28 only): request `WRITE_EXTERNAL_STORAGE`, then retry.
     - `Erro`: show a snackbar.
   - Badges on each card come from `RingtoneSetter.atuais()`. It compares each type's current system default with the last file the app saved for that type, which is kept in SharedPreferences `toques`. It's refreshed on `ON_RESUME`, so a sound the user picked manually in Sound settings also gets a badge.
3. The file is named after the raw resource it comes from, `game.audioPara(tipo)` (`<entry>.ogg`), and saved in the folder for its type (`Ringtones/`, `Notifications/` or `Alarms/`).

## Adding a new game ringtone

1. Add the audio file as `app/src/main/res/raw/<name>.ogg`. Use lowercase letters, digits and underscores only.
2. Add the title-screen image to `app/src/main/res/drawable-hdpi/`.
3. Add one constant to `MsxGame` in `MsxGame.kt`, with the display name, `Maquina`, `R.raw.<name>` and `R.drawable.<image>`.
4. Optional: add a short jingle (a few seconds) as `res/raw/<name>_notificacao.ogg` and pass it as the fifth argument, `notificacaoId`. **Set as… → Notification** then saves the jingle instead of the full track. Ringtone, alarm and in-app playback always use the full track.

You don't need to change anything else: the list, playback and ringtone code all read from the enum. Update the game counts in `MsxGameTest`.

Audio conventions: Ogg Vorbis, 44.1 kHz stereo (`ffmpeg -i in.mp3 -map 0:a:0 -vn -map_metadata -1 -ar 44100 -ac 2 -c:a libvorbis -q:a 5 out.ogg`). Trim leading silence. Keep tracks under about 75 s, cutting long ones to around 45 s with a 3 s fade-out. Title screens are 256×212 PNGs (the MSX2 screen size); pad shorter crops with black rather than stretching them.

## Known issues and gotchas

- The launcher icon is generated by `tools/generate_icon.py` (needs Pillow). It writes the adaptive vectors, the legacy PNGs and `play/icon-512.png`. Edit the pixel grid in the script and rerun it, rather than editing the outputs by hand.
- On Android 10+, a ringtone file created by a *previous install* of the app is not visible to the new install. In that case MediaStore saves the new copy as `name (1).ogg`. This is harmless.
- Version 2 has no Exit button and no goodbye toast. Back/home is the Android way to leave the app.
- Game images are pixel art: keep `FilterQuality.None` so they scale up without blurring. The MSX1/MSX2 logos are anti-aliased artwork, so the home screen scales them smoothly (`FilterQuality.High`).
- The MSX2 logo has a dark-theme variant in `drawable-night-hdpi/` (white "2"). Logo letters must be opaque white, not transparent, or they take the card color in dark theme.
- Audio in `res/raw` must be audio-only Ogg Vorbis. MP3s with cover art convert to Ogg with a Theora video stream unless you pass `-map 0:a:0 -vn` to ffmpeg, and `MediaPlayer` won't play those. `MsxGameTest.oggsSaoSoAudioVorbis` checks this.

## Conventions

- Kotlin official style, 4-space indent.
- Composables take state and callbacks as parameters. Only `MsxApp` owns navigation and the player.
- Icons are local vector drawables, so don't add `material-icons-*`.
- Put user-facing text in `strings.xml`, never inline. The app is English only. Use `stringResource` in composables and `LocalResources.current.getString` in callbacks and coroutines. Lint rejects `LocalContext.current.getString`.
- Guard newer platform APIs with `Build.VERSION.SDK_INT` checks and `@RequiresApi`.
- Logging uses `android.util.Log` with short tags (`msx player`, `msx ringtone`).
- When releasing, bump `versionCode` and `versionName` in `app/build.gradle`.
- Don't commit APKs, `build/` outputs or signing keys.
- `play/` holds Play Store material: `icon-512.png` and `LISTING.md`, which has the release notes, Data safety answers and descriptions.
- `PRIVACY.md` is the app's privacy policy, linked from the About screen and from Google Play. If you add a permission, a network call or any data collection, update it in the same change.

## Verifying changes

There are unit tests but no instrumentation tests. At a minimum:

1. `./gradlew :app:testDebugUnitTest`, `./gradlew :app:assembleDebug` and `./gradlew :app:lint` must succeed. Also build `:app:assembleRelease` to make sure R8 is happy.
2. If you have a device or emulator, install the app with `adb install -r app/build/outputs/apk/debug/app-debug.apk`. Then check:
   - Play and stop work.
   - "Set ringtone" sends you to "Modify system settings" the first time, and finishes the job when you come back.
   - Both light and dark themes look right.
   - Rotating the device or using a tablet layout works.

   Test on API 36 and, if possible, on API 28 or below.
3. To watch the logs, run `adb logcat -s "msx player" "msx ringtone"`.
