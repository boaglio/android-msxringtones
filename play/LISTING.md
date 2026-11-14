# Google Play listing — MSX Ringtones 2.0

Use this checklist to update the Play Console for version 2.0 (versionCode 12).

## 1. Upload the release

Build a signed App Bundle with your upload key, then upload it to **Production → Create new release**. Android Studio can do this with **Build → Generate Signed App Bundle**. From the command line:

```bash
./gradlew :app:bundleRelease
# then sign app/build/outputs/bundle/release/app-release.aab with your upload key (jarsigner or apksigner)
```

### Release notes (en-US)

```
Version 2.0 — a complete rewrite
• New Material 3 design, with Material You colors and dark theme
• Works on tablets and foldables, in portrait and landscape
• Gradius 2 and Salamander can now be set as ringtones
• Setting a ringtone works on the latest Android versions
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
PSG and FM-PAC for your ears!

MSX Ringtones brings the soundtracks of classic MSX1 and MSX2 games to your phone. Listen to any of them and set your favorite as your ringtone with one tap.

MSX1: Antartic Adventure, Goonies, Guardic, King's Valley, Gradius 2, Pacmania, Pippols, Salamander, Zanac.
MSX2: Aleste, Firehawk, Fray, Golvellius, Metal Gear, SD Snatcher, Treasure of USAS.

• Material 3 design with Material You colors and dark theme
• Works on phones, tablets and foldables
• No ads, no tracking, no data collected, no Internet permission
```
