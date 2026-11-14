# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project overview

**MSX Ringtones** is a small Android app, published on Google Play as `com.olimposystems.android`. Users can listen to soundtracks from classic MSX1 and MSX2 games and set one as the phone's default ringtone.

- Language: **Kotlin**. UI is **Jetpack Compose** with **Material 3**.
- The app has a single activity, `MSXringtonesActivity`. Screens are composables, and navigation is a simple screen stack in `ui/MsxApp.kt`, so there is no Navigation library.
- Single Gradle module: `:app`.
- Unit tests: `app/src/test/` (JUnit 4, JVM-only). Run them with `./gradlew :app:testDebugUnitTest`.
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
| Java | source/target 17; run Gradle with JDK 17+ |
| Dependencies | Compose BOM, material3, activity-compose, core-ktx, core-splashscreen, lifecycle-runtime-compose (`app/build.gradle`) |
| versionCode / versionName | in `app/build.gradle` (currently 11 / 1.11). The About screen reads `BuildConfig.VERSION_NAME` |

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease   # R8 + resource shrinking on; unsigned, signing is configured outside the repo
./gradlew :app:lint
```

- `local.properties` is committed, but it contains a machine-specific `sdk.dir`. Do not change it for your own environment. Set `ANDROID_HOME` instead.
- When you upgrade Kotlin, change both plugin versions in the root `build.gradle` together.

## Source layout

```
app/src/main/java/com/olimposystems/android/
├── MSXringtonesActivity.kt   # Only activity: splash screen, edge-to-edge, setContent { MsxTheme { MsxApp() } }.
│                             # The class name is kept from 1.x so existing launcher shortcuts keep working.
├── MsxGame.kt                # enum Maquina (MSX1/MSX2) + enum MsxGame (name, machine, R.raw, R.drawable)
├── audio/RingtonePlayer.kt   # One MediaPlayer at a time, with audio focus. `tocando` is Compose state
├── util/RingtoneSetter.kt    # suspend define(): saves .ogg to Ringtones/ (MediaStore on API 29+,
│                             # file path on 23-28) and sets it as the default ringtone. Returns a Resultado
└── ui/
    ├── MsxApp.kt             # Screen stack (Tela enum), BackHandler, shared RingtonePlayer
    ├── HomeScreen.kt         # MSX1 / MSX2 cards
    ├── GameListScreen.kt     # Adaptive grid of game cards + permission flow (dialog, launcher, snackbar)
    ├── AboutScreen.kt        # Title, logo, privacy-policy link, version
    ├── Comum.kt              # BarraSuperior (top app bar), PaddingValues.mais()
    └── theme/Theme.kt        # MsxTheme: dynamic color on 12+, MSX-blue fallback, dark theme
app/src/main/res/
├── raw/*.ogg                 # The ringtones
├── drawable-hdpi/            # Game title screens, msx1_default / msx2_default logos, olimposystems
├── drawable/                 # Vector icons (ic_play, ic_stop, ic_check, ic_info, ic_arrow_back, monochrome)
├── drawable-nodpi/ic_launcher_art.png  # Launcher art (72 px), used by the adaptive icon and splash
├── mipmap-anydpi-v26/ic_launcher.xml   # Adaptive icon (background, foreground, monochrome)
├── mipmap-mdpi/ic_launcher.png         # Legacy icon for API 23-25
└── values/, values-night/    # strings, colors, Theme.MSX / Theme.MSX.Starting (splash)
```

### Runtime flow

1. `MsxApp` holds a saveable stack of `Tela` values. Back and predictive-back gestures pop it through `BackHandler`. Changing screens or `ON_STOP` stops playback.
2. `GameListScreen` shows `MsxGame.da(maquina)` in a `LazyVerticalGrid` with adaptive columns, so it shows one column on phones and more on tablets and foldables.
   - The play/stop toggle calls `player.alterna(game)`.
   - **Set ringtone** calls `RingtoneSetter.define(context, game)` on `Dispatchers.IO`. The result is handled like this:
     - `PrecisaPermissaoConfiguracoes`: show a dialog that opens `ACTION_MANAGE_WRITE_SETTINGS`. On `ON_RESUME`, if the permission is now granted, the pending game is set automatically.
     - `PrecisaPermissaoArmazenamento` (API 23-28 only): request `WRITE_EXTERNAL_STORAGE`, then retry.
     - `Ok` / `Erro`: show a snackbar.
3. The ringtone file is named after the raw resource (`<entry>.ogg`) and saved in `Ringtones/`.

## Adding a new game ringtone

1. Add the audio file as `app/src/main/res/raw/<name>.ogg`. Use lowercase letters, digits and underscores only.
2. Add the title-screen image to `app/src/main/res/drawable-hdpi/`.
3. Add one constant to `MsxGame` in `MsxGame.kt`, with the display name, `Maquina`, `R.raw.<name>` and `R.drawable.<image>`.

You don't need to change anything else: the list, playback and ringtone code all read from the enum. Update the game counts in `MsxGameTest`.

## Known issues and gotchas

- The launcher art is a 72 px bitmap scaled into the adaptive icon, so it looks soft. Replace `drawable-nodpi/ic_launcher_art.png` with a 432×432 PNG or a vector. The monochrome (themed) layer is a generic music note.
- On Android 10+, a ringtone file created by a *previous install* of the app is not visible to the new install. In that case MediaStore saves the new copy as `name (1).ogg`. This is harmless.
- Version 2 has no Exit button and no goodbye toast. Back/home is the Android way to leave the app.
- Game images are pixel art: keep `FilterQuality.None` so they scale up without blurring.

## Conventions

- Kotlin official style, 4-space indent.
- Composables take state and callbacks as parameters. Only `MsxApp` owns navigation and the player.
- Icons are local vector drawables, so don't add `material-icons-*`.
- Put user-facing text in `strings.xml`, never inline. The app is English only. Use `stringResource` in composables and `LocalResources.current.getString` in callbacks and coroutines. Lint rejects `LocalContext.current.getString`.
- Guard newer platform APIs with `Build.VERSION.SDK_INT` checks and `@RequiresApi`.
- Logging uses `android.util.Log` with short tags (`msx player`, `msx ringtone`).
- When releasing, bump `versionCode` and `versionName` in `app/build.gradle`.
- Don't commit APKs, `build/` outputs or signing keys.

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
