# Build APK NOW - 3 Commands

## Prerequisites (One-time setup)

**Install Java 17+:**
- https://www.oracle.com/java/technologies/downloads/

**Install Android SDK:**
- Download Android Studio: https://developer.android.com/studio
- Or install SDK tools manually

**Set environment variable (if needed):**
```bash
# Mac/Linux
export ANDROID_SDK_ROOT=/path/to/android/sdk

# Windows
setx ANDROID_SDK_ROOT C:\path\to\android\sdk
```

---

## Build APK (3 Simple Steps)

### **Mac/Linux:**
```bash
./build-apk.sh
```

### **Windows:**
```bash
build-apk.bat
```

### **Any OS (Manual):**
```bash
./gradlew assembleDebug
```

---

## That's it!

APK will be at: `app/build/outputs/apk/debug/app-debug.apk`

**Install on device:**
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## Build Time
- First build: 3-5 minutes (downloads dependencies)
- Subsequent builds: 30-60 seconds

---

## Troubleshooting

**Issue: gradle: command not found**
- Use: `./gradlew` instead of `gradle`

**Issue: ANDROID_SDK_ROOT not set**
- Install Android SDK first
- Then set environment variable

**Issue: Java not found**
- Install JDK 17+

**Still having issues?** 
- See: BUILD_APK.md for detailed help
