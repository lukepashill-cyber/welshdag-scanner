# WelshDAG Scanner - Quick Start Guide

## 5-Minute Setup

### Step 1: Create Project Structure (1 min)

```bash
# Create the base directory
mkdir -p WelshDagScanner
cd WelshDagScanner

# Create all necessary directories
mkdir -p app/src/main/kotlin/com/welshdag/scanner/{data,di,network,security,ui/{screens,theme,viewmodel}}
mkdir -p app/src/main/res/values
mkdir -p gradle
```

### Step 2: Add Build Files (1 min)

Copy these files to the project root:
- `build.gradle.kts` → Root project (rename `root_build.gradle.kts`)
- `settings.gradle.kts` → Root project
- `libs.versions.toml` → `gradle/libs.versions.toml`

### Step 3: Add Source Files (2 min)

Copy Kotlin files to their directories:

**Root Package** (`app/src/main/kotlin/com/welshdag/scanner/`):
- `MainActivity.kt`
- `WelshDagApp.kt`

**Data Layer** (`app/src/main/kotlin/com/welshdag/scanner/data/`):
- `RpcRepository.kt`

**DI** (`app/src/main/kotlin/com/welshdag/scanner/di/`):
- `NetworkModule.kt`

**Network** (`app/src/main/kotlin/com/welshdag/scanner/network/`):
- `RpcService.kt`
- `RpcModels.kt`

**Security** (`app/src/main/kotlin/com/welshdag/scanner/security/`):
- `WalletStorage.kt`

**UI Screens** (`app/src/main/kotlin/com/welshdag/scanner/ui/screens/`):
- `HomeScreen.kt`
- `WalletConnectScreen.kt`
- `BalanceScreen.kt`

**UI Theme** (`app/src/main/kotlin/com/welshdag/scanner/ui/theme/`):
- `Theme.kt`
- `Type.kt`

**UI ViewModels** (`app/src/main/kotlin/com/welshdag/scanner/ui/viewmodel/`):
- `WalletViewModel.kt`
- `BalanceViewModel.kt`

### Step 4: Add Android Configuration (1 min)

**AndroidManifest.xml** → `app/src/main/AndroidManifest.xml`

**strings.xml** → `app/src/main/res/values/strings.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">WelshDAG Scanner</string>
</resources>
```

**themes.xml** → `app/src/main/res/values/themes.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.WelshDagScanner" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

### Step 5: Build & Run (varies)

```bash
# Build the app
./gradlew build

# Install on connected device/emulator
./gradlew installDebug

# Or open in Android Studio and click Run
```

## Gradle Sync Issues?

If you get Gradle errors:

```bash
# Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies

# If still issues:
# 1. File → Invalidate Caches → Restart in Android Studio
# 2. Update Android SDK to 34+
# 3. Update Android Studio to latest version
```

## Testing the App

1. **Home Screen**: Should see "WelshDAG Scanner" title with two buttons
2. **Connect Wallet**: 
   - Click "Generate New Wallet" → Should create and show address
   - OR Click "Import Private Key" → Paste a 64-char hex key
3. **Check Balance**:
   - Click "Check Balance" after wallet connection
   - Should query 4 RPC endpoints
   - Display balance or "Error"/"Offline" for failed endpoints

## App Structure at Runtime

```
Home Screen
├── Generate New Wallet
│   └── Shows connected wallet
│       └── Check Balance → Balance Screen
│       └── Disconnect
└── Import Private Key
    └── Shows connected wallet
        └── Check Balance → Balance Screen
        └── Disconnect
```

## Key Features to Test

✅ Wallet generation works
✅ Private key import works  
✅ Address validation catches invalid keys
✅ Balance queries return data from all 4 endpoints
✅ App handles offline RPC endpoints gracefully
✅ Wallet persists across app restarts
✅ Dark mode works (system theme)
✅ Scrolling works on long addresses

## File Summary

Total files to create: **21 Kotlin files + 6 config files**

### Kotlin Files (15 in app/)
- 2 App files
- 1 Network data file
- 2 Network interface/model files
- 1 DI module
- 1 Repository
- 1 Security storage
- 3 UI screens
- 2 Theme files
- 2 ViewModels

### Config Files (6)
- 1 Manifest
- 1 App build gradle
- 1 Root build gradle
- 1 Settings gradle
- 1 Version catalog
- 2 Resource files (strings, themes)

## Troubleshooting Quick Fixes

| Issue | Fix |
|-------|-----|
| `Cannot find symbol MainActivity` | Check package name matches `com.welshdag.scanner` |
| `Hilt error: no @HiltViewModel` | Ensure viewmodel class has `@HiltViewModel` annotation |
| `RPC endpoints not loading` | Check internet permission in manifest ✓ |
| `App crashes on startup` | Run `./gradlew clean` and rebuild |
| `Gradle can't find dependencies` | Update Android SDK to latest |

## What You Can Do Next

Once running:
- ✨ Customize colors in `Theme.kt`
- ✨ Add more RPC endpoints in `RpcRepository.kt`
- ✨ Add transaction history feature
- ✨ Add send transaction feature
- ✨ Add QR code scanner

## Questions?

Check `README.md` for detailed documentation, architecture info, and security details.

Happy scanning! 🚀
