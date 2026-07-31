# WelshDAG Scanner - Complete File Placement Guide

## Project Root Structure

```
WelshDagScanner/
├── gradle/
│   └── libs.versions.toml
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/welshdag/scanner/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts (root)
├── settings.gradle.kts
└── README.md
```

## File Placement Table

| File Name | Destination | Type | Purpose |
|-----------|-------------|------|---------|
| `build.gradle.kts` | `app/build.gradle.kts` | Config | App-level Gradle build |
| `root_build.gradle.kts` | `build.gradle.kts` | Config | Root Gradle build |
| `settings.gradle.kts` | `settings.gradle.kts` | Config | Gradle settings |
| `libs.versions.toml` | `gradle/libs.versions.toml` | Config | Dependency versions |
| `proguard-rules.pro` | `app/proguard-rules.pro` | Config | ProGuard obfuscation rules |
| `AndroidManifest.xml` | `app/src/main/AndroidManifest.xml` | Config | Android app manifest |

## Kotlin Source Files

### Application Entry Points
| File | Path | Purpose |
|------|------|---------|
| `WelshDagApp.kt` | `app/src/main/kotlin/com/welshdag/scanner/WelshDagApp.kt` | Hilt application class |
| `MainActivity.kt` | `app/src/main/kotlin/com/welshdag/scanner/MainActivity.kt` | Main activity with navigation |

### Data Layer
| File | Path | Purpose |
|------|------|---------|
| `RpcRepository.kt` | `app/src/main/kotlin/com/welshdag/scanner/data/RpcRepository.kt` | RPC operations & balance fetching |

### Network Layer
| File | Path | Purpose |
|------|------|---------|
| `RpcService.kt` | `app/src/main/kotlin/com/welshdag/scanner/network/RpcService.kt` | Retrofit interface for RPC calls |
| `RpcModels.kt` | `app/src/main/kotlin/com/welshdag/scanner/network/RpcModels.kt` | Data classes for RPC requests/responses |

### Dependency Injection
| File | Path | Purpose |
|------|------|---------|
| `NetworkModule.kt` | `app/src/main/kotlin/com/welshdag/scanner/di/NetworkModule.kt` | Hilt DI modules for networking |

### Security & Storage
| File | Path | Purpose |
|------|------|---------|
| `WalletStorage.kt` | `app/src/main/kotlin/com/welshdag/scanner/security/WalletStorage.kt` | Encrypted wallet storage |

### UI Screens
| File | Path | Purpose |
|------|------|---------|
| `HomeScreen.kt` | `app/src/main/kotlin/com/welshdag/scanner/ui/screens/HomeScreen.kt` | Home/entry screen |
| `WalletConnectScreen.kt` | `app/src/main/kotlin/com/welshdag/scanner/ui/screens/WalletConnectScreen.kt` | Wallet connection/import screen |
| `BalanceScreen.kt` | `app/src/main/kotlin/com/welshdag/scanner/ui/screens/BalanceScreen.kt` | Balance display screen |

### UI Theme & Styling
| File | Path | Purpose |
|------|------|---------|
| `Theme.kt` | `app/src/main/kotlin/com/welshdag/scanner/ui/theme/Theme.kt` | Material3 color scheme & theme |
| `Type.kt` | `app/src/main/kotlin/com/welshdag/scanner/ui/theme/Type.kt` | Typography definitions |

### ViewModels
| File | Path | Purpose |
|------|------|---------|
| `WalletViewModel.kt` | `app/src/main/kotlin/com/welshdag/scanner/ui/viewmodel/WalletViewModel.kt` | Wallet connection/generation logic |
| `BalanceViewModel.kt` | `app/src/main/kotlin/com/welshdag/scanner/ui/viewmodel/BalanceViewModel.kt` | Balance fetching logic |

## Resource Files

### Values
| File | Path | Content |
|------|------|---------|
| `strings.xml` | `app/src/main/res/values/strings.xml` | String resources |
| `themes.xml` | `app/src/main/res/values/themes.xml` | Theme resources |

### Directory Structure
```
app/src/main/res/
├── values/
│   ├── strings.xml
│   └── themes.xml
└── mipmap/
    └── ic_launcher.xml (system-generated)
```

## Quick Directory Creation Script

```bash
#!/bin/bash
# Run this from the project root

mkdir -p gradle
mkdir -p app/src/main/kotlin/com/welshdag/scanner/data
mkdir -p app/src/main/kotlin/com/welshdag/scanner/di
mkdir -p app/src/main/kotlin/com/welshdag/scanner/network
mkdir -p app/src/main/kotlin/com/welshdag/scanner/security
mkdir -p app/src/main/kotlin/com/welshdag/scanner/ui/screens
mkdir -p app/src/main/kotlin/com/welshdag/scanner/ui/theme
mkdir -p app/src/main/kotlin/com/welshdag/scanner/ui/viewmodel
mkdir -p app/src/main/res/values
```

## Total File Count

- **Gradle/Config Files**: 6
  - build.gradle.kts (root)
  - app/build.gradle.kts
  - settings.gradle.kts
  - gradle/libs.versions.toml
  - app/proguard-rules.pro
  - AndroidManifest.xml

- **Kotlin Source Files**: 15
  - 2 app files
  - 1 data file
  - 2 network files
  - 1 DI file
  - 1 security file
  - 3 screen files
  - 2 theme files
  - 2 viewmodel files
  - 1 models file

- **Resource Files**: 2
  - strings.xml
  - themes.xml

- **Documentation**: 3
  - README.md
  - QUICK_START.md
  - FILE_PLACEMENT.md (this file)

**Total: 26 files**

## Verification Checklist

After placing all files, verify:

- [ ] All Kotlin files are in correct `com/welshdag/scanner/` subdirectories
- [ ] build.gradle.kts is in `app/` directory
- [ ] Root build.gradle.kts (renamed) is in project root
- [ ] settings.gradle.kts is in project root
- [ ] libs.versions.toml is in `gradle/` directory
- [ ] AndroidManifest.xml is in `app/src/main/`
- [ ] strings.xml is in `app/src/main/res/values/`
- [ ] themes.xml is in `app/src/main/res/values/`
- [ ] proguard-rules.pro is in `app/` directory

## Gradle Sync

After all files are in place:

1. Open the project in Android Studio
2. File → Sync with Gradle Files
3. Or run: `./gradlew build`
4. If errors, check:
   - Package names are `com.welshdag.scanner`
   - All directories exist
   - No typos in file names

## Common Mistakes to Avoid

❌ Putting Kotlin files in wrong package
❌ build.gradle.kts in root instead of app/
❌ Missing `gradle/` directory for libs.versions.toml
❌ AndroidManifest.xml in wrong location
❌ Forgetting to rename root_build.gradle.kts
❌ Resource files in wrong res/ subdirectory

## Next Steps After Setup

1. ✅ Place all files
2. ✅ Run `./gradlew build`
3. ✅ Open in Android Studio
4. ✅ Click Run to install on device/emulator
5. ✅ Test the app

Good luck! 🚀
