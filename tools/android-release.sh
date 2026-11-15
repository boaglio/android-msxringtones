#!/usr/bin/env bash
# Builds the Android App Bundle for Google Play, signed with your key, and checks that your key really signed it.
#
# Usage: tools/android-release.sh [gradle options]
#
# The settings are in release.env, which git ignores: copy release.env.example and fill it in.
# Output: release/MSXRingtones-<version>-<code>.aab
set -euo pipefail

die() { echo "android-release: $*" >&2; exit 1; }

root=$(cd "$(dirname "$0")/.." && pwd)
env_file=$root/release.env
[ -f "$env_file" ] || die "no $env_file: copy release.env.example and fill it in"

set -a
. "$env_file"
set +a

for name in ANDROID_HOME ANDROID_KEYSTORE_FILE ANDROID_KEYSTORE_PASSWORD ANDROID_KEY_ALIAS ANDROID_KEY_PASSWORD; do
    [ -n "${!name:-}" ] || die "$name is not set in $env_file"
done
[ -d "$ANDROID_HOME" ] || die "ANDROID_HOME is not a folder: $ANDROID_HOME"
case $ANDROID_KEYSTORE_FILE in
    /*) ;;
    *) ANDROID_KEYSTORE_FILE=$root/$ANDROID_KEYSTORE_FILE ;; # relative paths start at the repository
esac
export ANDROID_KEYSTORE_FILE
[ -f "$ANDROID_KEYSTORE_FILE" ] || die "no keystore at $ANDROID_KEYSTORE_FILE"

keytool=keytool
[ -z "${JAVA_HOME:-}" ] || keytool=$JAVA_HOME/bin/keytool
command -v "$keytool" >/dev/null || die "keytool not found: set JAVA_HOME in $env_file"

# The passwords stay in the environment, so they never appear in the list of running processes.
fingerprint() { sed -n 's/^[[:space:]]*SHA256: *//p' | head -n 1; }
key=$("$keytool" -J-Duser.language=en -list -v -keystore "$ANDROID_KEYSTORE_FILE" -alias "$ANDROID_KEY_ALIAS" \
    -storepass:env ANDROID_KEYSTORE_PASSWORD) \
    || die "cannot read key \"$ANDROID_KEY_ALIAS\": check the alias and ANDROID_KEYSTORE_PASSWORD"
expected=$(fingerprint <<<"$key")
[ -n "$expected" ] || die "cannot read key \"$ANDROID_KEY_ALIAS\": check the alias and ANDROID_KEYSTORE_PASSWORD"

# The key Google Play expects (Play Console → Test and release → App integrity). Checking it here catches a
# wrong keystore before the upload does.
if [ -n "${ANDROID_EXPECTED_SHA1:-}" ]; then
    sha1=$(sed -n 's/^[[:space:]]*SHA1: *//p' <<<"$key" | head -n 1)
    [ "${sha1^^}" = "${ANDROID_EXPECTED_SHA1^^}" ] \
        || die "wrong key: Google Play expects SHA1 $ANDROID_EXPECTED_SHA1, \"$ANDROID_KEY_ALIAS\" is ${sha1:-unknown}"
fi

# New keys must be RSA with 2048 bits or more. An app that was first published with a weaker key (MSX Ringtones
# 1.x used 1024-bit RSA) must keep signing with it, so this is only a warning.
strength=$(sed -n 's/^Subject Public Key Algorithm: //p' <<<"$key" | head -n 1)
bits=0
case $strength in
    *RSA*) bits=${strength%%-bit*} ;;
esac
if ! [ "$bits" -ge 2048 ] 2>/dev/null; then
    echo "android-release: warning: weak key (${strength:-unknown}). Google Play still accepts it for this app," >&2
    echo "android-release: but consider Play App Signing with a new 2048-bit upload key (see play/LISTING.md)." >&2
fi

gradle_file=$root/app/build.gradle
version=$(sed -n 's/^[[:space:]]*versionName = "\(.*\)"/\1/p' "$gradle_file" | head -n 1)
code=$(sed -n 's/^[[:space:]]*versionCode = \([0-9]*\)/\1/p' "$gradle_file" | head -n 1)
[ -n "$version" ] && [ -n "$code" ] || die "cannot read versionName/versionCode from $gradle_file"

built=$root/app/build/outputs/bundle/release/app-release.aab
bundle=$root/release/MSXRingtones-$version-$code.aab
rm -f "$built" "$bundle" # never leave an older bundle behind if this build fails
(cd "$root" && ./gradlew --no-daemon "$@" :app:testDebugUnitTest :app:lintRelease :app:bundleRelease)
[ -f "$built" ] || die "the build did not produce $built"

actual=$("$keytool" -J-Duser.language=en -printcert -jarfile "$built" | fingerprint)
[ "$actual" = "$expected" ] || die "the bundle is not signed with your key: expected $expected, found ${actual:-none}"

mkdir -p "$root/release"
cp "$built" "$bundle"

echo
echo "Version: $version (versionCode $code)"
echo "Signed with: $expected"
echo "Upload this to Google Play: $bundle"
