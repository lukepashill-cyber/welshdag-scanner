# Building WelshDAG Scanner APK

## Quick Build (3 Minutes)

### Option 1: Android Studio (Easiest)

1. **Open Project**
   - Launch Android Studio
   - File → Open → Select this WelshDagScanner folder
   - Wait for Gradle sync to complete (~2 min)

2. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Wait for build to complete (1-2 minutes)

3. **APK Location**
   - `app/build/outputs/apk/debug/app-debug.apk`
   - **Install on device:** `adb install app-debug.apk`
   - **Or drag to Android Studio emulator**

---

### Option 2: Command Line (No Android Studio needed)

#### Prerequisites
```bash
# Must have Android SDK installed
# And ANDROID_SDK_ROOT environment variable set

# Verify:
echo $ANDROID_SDK_ROOT
# Should output path like: /path/to/android/sdk
```

#### Build Debug APK
```bash
cd WelshDagScanner

# Option A: Using Gradle wrapper (no gradle installation needed)
./gradlew assembleDebug

# Option B: Using installed gradle
gradle assembleDebug
```

#### Find APK
```bash
# APK will be at:
app/build/outputs/apk/debug/app-debug.apk
```

#### Install APK
```bash
# Connect device or emulator, then:
adb install app/build/outputs/apk/debug/app-debug.apk

# Or for reinstall:
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

### Option 3: Release APK (Signed)

#### Create Keystore (first time only)
```bash
keytool -genkey -v -keystore ~/welshdag.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias welshdag-key
```

#### Build Release APK
```bash
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=~/welshdag.jks \
  -Pandroid.injected.signing.store.password=yourpassword \
  -Pandroid.injected.signing.key.alias=welshdag-key \
  -Pandroid.injected.signing.key.password=yourpassword
```

#### Find Release APK
```bash
app/build/outputs/apk/release/app-release.apk
```

#### Install Release APK
```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

---

## System Requirements

### Windows/Mac/Linux
- **Java:** JDK 17 or higher
- **Android SDK:** API 26+ installed
- **RAM:** 4GB minimum (8GB recommended)
- **Disk:** 2GB free space for build files

### Verify Setup
```bash
# Check Java
java -version
# Should show: java version 17 or higher

# Check Android SDK
$ANDROID_SDK_ROOT/tools/bin/sdkmanager --version
# Or on Windows:
%ANDROID_SDK_ROOT%\tools\bin\sdkmanager.bat --version
```

---

## Troubleshooting Build Issues

### Issue: "gradle: command not found"
**Solution:** Use the Gradle wrapper included in project
```bash
./gradlew assembleDebug    # On Mac/Linux
gradlew.bat assembleDebug  # On Windows
```

### Issue: "ANDROID_SDK_ROOT is not set"
**Solution:** Set environment variable
```bash
# On Mac/Linux
export ANDROID_SDK_ROOT=/path/to/android/sdk
echo "export ANDROID_SDK_ROOT=/path/to/android/sdk" >> ~/.bash_profile

# On Windows
set ANDROID_SDK_ROOT=C:\path\to\android\sdk
# Or use System Settings → Environment Variables
```

### Issue: Build fails with "API 26 not found"
**Solution:** Install required SDK
```bash
# Download SDK 26+
$ANDROID_SDK_ROOT/tools/bin/sdkmanager "platforms;android-34"
```

### Issue: "Gradle sync failed"
**Solution:** 
```bash
# Clean and retry
./gradlew clean
./gradlew build

# If still fails:
# 1. Delete .gradle folder
# 2. Delete build folder
# 3. Retry gradlew build
```

### Issue: "Out of memory" during build
**Solution:** Increase Gradle memory
```bash
# Create gradle.properties in project root:
org.gradle.jvmargs=-Xmx2048m
```

---

## Build Output Files

### After `./gradlew assembleDebug`
```
app/build/outputs/apk/debug/
├── app-debug.apk          ← This is what you install
└── app-debug-symbols.apk
```

### After `./gradlew assembleRelease`
```
app/build/outputs/apk/release/
├── app-release.apk        ← This is what you distribute
└── app-release-symbols.apk
```

### APK Size
- Debug APK: ~20-25 MB
- Release APK: ~15-18 MB (optimized)

---

## Installing & Testing the APK

### Via Android Studio
1. Build → Analyze APK
2. Select APK file
3. View contents and sizes
4. Run → Run 'app'

### Via Command Line (adb)
```bash
# Install
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Run
adb shell am start -n com.welshdag.scanner/.MainActivity

# View logs
adb logcat

# Uninstall
adb uninstall com.welshdag.scanner
```

### Via Device Explorer
1. Android Studio → Device Explorer (right sidebar)
2. Navigate to app/build/outputs/apk/debug/
3. Right-click app-debug.apk → Install APK
4. Select connected device

---

## Build Time Expectations

| Task | Time |
|------|------|
| First build | 3-5 minutes |
| Subsequent builds | 30-60 seconds |
| Clean rebuild | 3-5 minutes |
| Release build | 2-3 minutes |

---

## Verification Steps

After installing APK, verify:

1. **App launches** - No crash on startup
2. **Home screen visible** - Title and buttons visible
3. **Wallet generation** - Can generate new wallet
4. **Private key import** - Can import existing key
5. **Balance checking** - Can query 4 RPC endpoints
6. **Navigation** - Can navigate between screens
7. **Persistence** - Wallet remains after app restart

---

## Release Checklist

Before distributing APK:

- [ ] Built with release variant (not debug)
- [ ] Signed with production keystore
- [ ] ProGuard enabled (obfuscation active)
- [ ] Version number bumped
- [ ] Tested on multiple devices
- [ ] No debug logging in production
- [ ] Privacy policy included
- [ ] Permissions justified

---

## Common Build Commands

```bash
# Debug APK
./gradlew assembleDebug

# Release APK (unsigned)
./gradlew assembleRelease

# Bundle for Play Store
./gradlew bundleRelease

# Run on connected device
./gradlew installDebug
./gradlew installRelease

# Run tests
./gradlew test

# Clean build
./gradlew clean build

# See build info
./gradlew projects
./gradlew tasks
```

---

## Next Steps

1. ✅ Ensure prerequisites installed (Java 17+, Android SDK)
2. ✅ Run `./gradlew assembleDebug` OR use Android Studio Build menu
3. ✅ Find APK in `app/build/outputs/apk/debug/`
4. ✅ Install with `adb install app-debug.apk`
5. ✅ Launch app and test features

**Total time:** 3-5 minutes

---

## Support

- **Android Studio Issues:** Help → Check for Updates
- **Gradle Issues:** `./gradlew --version` and update if needed
- **SDK Issues:** Use `sdkmanager` to install missing components
- **Build Failures:** Check console output for specific errors

---

**Your APK will be ready in minutes!** 🚀

The app is now ready to be built into an installable APK file.
