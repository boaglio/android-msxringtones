# AGENTS.md

Guidance for AI coding agents working in this repository.

## Project overview

**MSX Ringtones** is a small Android app, published on Google Play as `com.olimposystems.android`. Users can listen to soundtracks from classic MSX1 and MSX2 games and set one as the phone's default ringtone.

- Language: Java. No Kotlin, no AndroidX, and no Jetpack.
- UI: classic `android.app.Activity` with XML layouts. No fragments are actually used: the `fragment_*` layouts are plain `<include>` files.
- Single Gradle module: `:app`.
- No tests exist.
- Code identifiers, log messages and comments are mostly in **Portuguese**. For example: `registraBotao` means "register button", `nome` means "name", `arquivoId` means "file id", `botao` means "button", `sobre` means "about", `sair` means "exit", and `tocando` means "playing". User-facing strings are in English. Keep that split.

## Build

| Item | Value |
|---|---|
| Android Gradle Plugin | 9.5.1 (`build.gradle`, plugins DSL; repos in `settings.gradle`) |
| Gradle | 9.9.1 via the committed wrapper (`./gradlew`) |
| compileSdk / targetSdk | 36 (Google Play requires 36 for updates since Aug 2026) |
| minSdk | 23 |
| Java | source/target 17; run Gradle with JDK 17+ |
| versionCode / versionName | in `app/build.gradle` (currently 11 / 1.11); keep `versao` in `strings.xml` in sync |

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease   # unsigned; signing is configured outside the repo
./gradlew :app:lint
```

- `local.properties` is committed, but it contains a machine-specific `sdk.dir`. Do not change it for your own environment. Set `ANDROID_HOME` instead.
- The app has no library dependencies: it uses only the platform SDK, with no AndroidX.

## Source layout

```
app/src/main/java/com/olimposystems/android/
├── MSXringtonesActivity.java   # Launcher: dashboard with MSX1 / MSX2 / Exit buttons
├── MSX1ringtonesActivity.java  # List of MSX1 games (play + set ringtone)
├── MSX2ringtonesActivity.java  # List of MSX2 games (play + set ringtone)
├── SobreActivity.java          # "About" screen; opens privacy-policy URL
├── DashboardLayout.java        # Custom ViewGroup grid used by dashboard_layout.xml
├── util/EdgeToEdge.java        # Pads each screen for system bars (edge-to-edge is forced on API 35+)
├── type/
│   ├── MSX1Game.java           # enum: display name, play-button id, ringtone-button id, R.raw id
│   └── MSX2Game.java           # same, for MSX2
└── util/
    └── RingtoneUtil.java       # Writes the raw .ogg into Ringtones/ (MediaStore on API 29+, file
                                # path on API 23-28) and sets it as the default ringtone
app/src/main/res/
├── raw/*.ogg                   # The ringtones themselves
├── layout/msx{1,2}_layout.xml  # Screen = actionbar_msx{1,2}_layout + fragment_msx{1,2}_layout + footer
├── layout/fragment_msx{1,2}_layout.xml  # TableLayout, one TableRow per game
├── drawable-hdpi/              # Game title images, icons (ic_launcher, speaker_logo, checkmark, about…)
└── values/strings.xml
```

### Runtime flow

1. `MSXringtonesActivity` opens `MSX1ringtonesActivity` or `MSX2ringtonesActivity`.
2. For each enum constant, the activity's `registraBotao*()` method connects two buttons:
   - **Play** (`botao_play_*`): releases any current `MediaPlayer`, then creates and starts a new one from `game.arquivoId()`.
   - **Use ringtone** (`botao_use_ringtone_*`): calls `RingtoneUtil.setRingtone(activity, game.arquivoId(), game.nome())`.
3. `RingtoneUtil.setRingtone`:
   - If `Settings.System.canWrite()` is false, it shows a dialog that opens `ACTION_MANAGE_WRITE_SETTINGS` and returns false.
   - On API 23-28, it also requests `WRITE_EXTERNAL_STORAGE` at runtime.
   - It then saves `<raw entry name>.ogg` into `Ringtones/` and calls `RingtoneManager.setActualDefaultRingtoneUri`.
   - It returns true on success. On failure, it has already shown a toast to the user.
4. Playback stops and the player is released in `onStop` and when the clip finishes.
5. Every activity calls `EdgeToEdge.aplica(this)` right after `setContentView`. New activities must do the same.

## Adding a new game ringtone

All of these steps are required:

1. Add the audio file as `app/src/main/res/raw/<name>.ogg`. Use lowercase letters, digits and underscores only.
2. Add the title image to `app/src/main/res/drawable-hdpi/`.
3. Add a `<TableRow>` to `fragment_msx1_layout.xml` or `fragment_msx2_layout.xml`. Copy an existing row. Each row has an `ImageView` with `texto_<id>`, an `ImageButton` with `botao_play_<id>`, and an `ImageButton` with `botao_use_ringtone_<id>`.
4. Add an enum constant to `MSX1Game` or `MSX2Game` with the display name, both button IDs and `R.raw.<name>`.
5. Call `registraBotao(...)` or `registraBotaoImagem(...)` for it in the matching activity's `onCreate`.
6. Optionally, add a `jogo_<id>` string to `strings.xml`.

`RingtoneUtil` needs no changes, because it works from the enum's raw resource ID.

## Known issues and gotchas

- On Android 10+, a ringtone file created by a *previous install* of the app is not visible to the new install. In that case MediaStore saves the new copy as `name (1).ogg`. This is harmless.
- Ringtone files are written on the UI thread. The largest file is about 1.5 MB, so this is acceptable for now.
- Playback does not request audio focus.
- Menu item IDs are the integer literals `1` (About) and `2` (Exit, launcher only).

## Conventions

- Indentation is mixed: tabs in the activities and `RingtoneUtil`, spaces in the enums. Follow the existing style of each file and avoid reformatting whole files.
- New code uses Java 8+ lambdas. Guard newer platform APIs with `Build.VERSION.SDK_INT` checks and mark the guarded helper methods `@TargetApi`.
- Logging uses `android.util.Log` with a per-class `CATEGORIA`/`CATEGORY` tag.
- When releasing, bump `versionCode` and `versionName` in `app/build.gradle`. Recent commit messages look like `version 10 - new policy` and `feat: update Android SDK 31`.
- Don't commit APKs, `build/` outputs or signing keys. `.gitignore` already covers `*.apk`, `*.jar` and `app/release/`.

## Verifying changes

There are no unit or instrumentation tests. At a minimum:

1. `./gradlew :app:assembleDebug` and `./gradlew :app:lint` must succeed.
2. If you have a device or emulator, install the app with `adb install -r app/build/outputs/apk/debug/app-debug.apk` and check that both lists play audio and that "use ringtone" works. Test this on API 36 and, if possible, on API 28 or below. On first use, it should send the user to the "Modify system settings" screen. You can watch the logs with `adb logcat -s msx msx1 msx2 "msx ringtone"`.
