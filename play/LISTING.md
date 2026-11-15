# Google Play listing — MSX Ringtones 2.0

Use this checklist to update the Play Console for version 2.0 (versionCode 12).

## 1. Upload the release

Build the signed App Bundle with the release script, then upload it to **Production → Create new release**:

```bash
cp release.env.example release.env   # once: fill in the SDK path and your signing key (git ignores release.env)
tools/android-release.sh
# → release/MSXRingtones-<version>-<versionCode>.aab
```

The script runs the unit tests and lint, builds the bundle and checks that it is signed with your key. Use the upload key if the app is enrolled in Play App Signing (**Test and release → App integrity**), otherwise the app's original 1.x key (SHA1 `29:03:9E:B7:31:D0:83:6D:28:63:F1:CC:A0:17:EC:4E:8E:93:F2:15`). Set `ANDROID_EXPECTED_SHA1` in `release.env` and the script refuses any other key.

The original key is 1024-bit RSA. Google Play still accepts it for this app, and the script only warns about it. To move to a stronger key, enroll in Play App Signing in **App integrity**, then request an upload key upgrade there and sign later releases with the new 2048-bit key. Keep the old keystore backed up, because it can't be recovered. Bump `versionCode` in `app/build.gradle` before every upload: Google Play rejects a versionCode it has already seen.

### Release notes (en-US, Google Play limit: 500 characters)

```
Version 2.0, a complete rewrite
• New games: Knightmare, The Maze of Galious, Nemesis 3 and Space Manbow
• Set any soundtrack as ringtone, notification or alarm
• Short jingles for notifications (Knightmare, Nemesis 3)
• Badges show which games you use, plus a progress bar
• New Material 3 design with dark theme
• Works on tablets and foldables
• Works on the latest Android versions
• New app icon
```

## 2. Store listing → Graphics

- **App icon (512×512):** `play/icon-512.png`
- **Phone screenshots:** take new ones on a phone running 2.0. The current ones show the 1.x design.

## 3. App content → Privacy policy

URL: `https://github.com/boaglio/android-msxringtones/blob/main/PRIVACY.md`

## 4. App content → Data safety

| Question | Answer |
|---|---|
| Does your app collect or share any of the required user data types? | **No** |
| Is all of the user data collected by your app encrypted in transit? | Not applicable (no data collected) |
| Do you provide a way for users to request that their data is deleted? | Not applicable (no data collected) |

Result shown on Play: **"No data collected · No data shared with third parties"**.

## 5. App content → other declarations

- **Ads:** No, the app does not contain ads.
- **App access:** All functionality is available without special access.
- **Target audience:** choose the age groups you want. The app collects no data, so the Families policy requirements about data are met.

## Short description (80 characters max)

```
Classic MSX1 and MSX2 game soundtracks as your phone ringtone.
```

## Full description

```
PSG, SCC and FM-PAC for your ears!

MSX Ringtones brings the soundtracks of classic MSX1 and MSX2 games to your phone. Listen to any of them and set your favorites as your ringtone, notification sound or alarm.

MSX1: Antarctic Adventure, Goonies, Guardic, King's Valley, Knightmare, The Maze of Galious, Gradius 2, Nemesis 3: The Eve of Destruction, Pac-Mania, Pippols, Salamander, Zanac.
MSX2: Aleste 2, Fire Hawk, Fray, Golvellius, Metal Gear, SD Snatcher, Space Manbow, Treasure of USAS.

• Material 3 design with Material You colors and dark theme
• Works on phones, tablets and foldables
• No ads, no tracking, no data collected, no Internet permission
```
