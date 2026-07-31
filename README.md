# WelshDAG Scanner - Android App

A modern Android app for checking WelshDAG wallet balances across multiple RPC endpoints with secure wallet management.

## Features

- 🔐 **Secure Wallet Management**
  - Generate new wallets
  - Import existing wallets via private key
  - Encrypted storage using Android Security Crypto
  - Private key never stored in plaintext

- 💰 **Multi-RPC Balance Checking**
  - Check balance against 4 RPC endpoints:
    - https://rpc.welshdag.trade
    - https://rpc.capedag.com
    - https://rpc.bdag-us.org
    - https://rpc.dvdmining.com
  - Automatic fallback if an endpoint is unavailable
  - Real-time balance updates

- 🎨 **Modern UI**
  - Jetpack Compose for responsive UI
  - Material Design 3
  - Dark/Light theme support
  - Smooth animations and transitions

## Project Structure

```
android-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/welshdag/scanner/
│   │   │   │   ├── MainActivity.kt           # App entry point
│   │   │   │   ├── WelshDagApp.kt           # Hilt application
│   │   │   │   ├── data/
│   │   │   │   │   └── RpcRepository.kt     # RPC operations
│   │   │   │   ├── di/
│   │   │   │   │   └── NetworkModule.kt     # Dependency injection
│   │   │   │   ├── network/
│   │   │   │   │   ├── RpcService.kt        # Retrofit interface
│   │   │   │   │   └── RpcModels.kt         # Data models
│   │   │   │   ├── security/
│   │   │   │   │   └── WalletStorage.kt     # Secure storage
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   ├── WalletConnectScreen.kt
│   │   │   │   │   │   └── BalanceScreen.kt
│   │   │   │   │   ├── theme/
│   │   │   │   │   │   ├── Theme.kt
│   │   │   │   │   │   └── Type.kt
│   │   │   │   │   └── viewmodel/
│   │   │   │   │       ├── WalletViewModel.kt
│   │   │   │   │       └── BalanceViewModel.kt
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml
└── build.gradle.kts
```

## Prerequisites

- Android Studio Flamingo or later
- Android SDK 26+ (target SDK 34)
- JDK 17+
- Gradle 8.0+

## Setup Instructions

### 1. Create Project Structure

```bash
# Create the main Android project directory
mkdir -p WelshDagScanner/app/src/main/kotlin/com/welshdag/scanner/{data,di,network,security,ui}
mkdir -p WelshDagScanner/app/src/main/resources/values
mkdir -p WelshDagScanner/gradle

cd WelshDagScanner
```

### 2. Copy Files

Copy all the provided Kotlin files to the appropriate directories:
- `MainActivity.kt`, `WelshDagApp.kt` → `app/src/main/kotlin/com/welshdag/scanner/`
- `RpcRepository.kt` → `app/src/main/kotlin/com/welshdag/scanner/data/`
- `NetworkModule.kt` → `app/src/main/kotlin/com/welshdag/scanner/di/`
- `RpcService.kt`, `RpcModels.kt` → `app/src/main/kotlin/com/welshdag/scanner/network/`
- `WalletStorage.kt` → `app/src/main/kotlin/com/welshdag/scanner/security/`
- Screen files → `app/src/main/kotlin/com/welshdag/scanner/ui/screens/`
- Theme files → `app/src/main/kotlin/com/welshdag/scanner/ui/theme/`
- ViewModel files → `app/src/main/kotlin/com/welshdag/scanner/ui/viewmodel/`
- `AndroidManifest.xml` → `app/src/main/`

### 3. Create Resource Files

#### strings.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">WelshDAG Scanner</string>
</resources>
```

#### themes.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.WelshDagScanner" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

### 4. Build Files

Copy the provided `build.gradle.kts` and `libs.versions.toml` files to the project root.

Create `settings.gradle.kts`:
```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "WelshDAG Scanner"
include(":app")
```

### 5. Build the App

```bash
# Build debug APK
./gradlew build

# Build release APK
./gradlew assembleRelease

# Run on emulator/device
./gradlew installDebug
```

## Usage Guide

### Home Screen
- Launch the app to see the home screen
- Choose "Connect Wallet" or "Import Wallet"

### Wallet Connection
- **Generate New Wallet**: Creates a new random wallet and encrypts the private key
- **Import Private Key**: Paste your existing private key (with or without "0x" prefix)
- Private keys are encrypted using Android's Security Crypto library

### Balance Checking
- After connecting a wallet, tap "Check Balance"
- The app queries all 4 RPC endpoints simultaneously
- Displays balance for each endpoint
- Shows online/offline status
- Tap refresh to update balances
- Navigate back to disconnect or import another wallet

## Security Considerations

### Private Key Management
- Private keys are encrypted with Android's `EncryptedSharedPreferences`
- Uses AES-256-GCM for encryption
- Keys are derived from the device's master key
- Private keys are **never** logged or transmitted to external servers

### Network Security
- All RPC endpoints are called via HTTPS
- Uses OkHttp with certificate pinning (optional enhancement)
- HTTP request logging is enabled in debug builds

### Best Practices
- Never share your private key
- Only import wallets on trusted devices
- Use the app's wallet generation feature for new wallets
- Disconnect wallet when not in use

## API Reference

### RPC Endpoints

The app supports checking balances on these endpoints:
- `https://rpc.welshdag.trade` - Primary WelshDAG RPC
- `https://rpc.capedag.com` - Cape DAG RPC
- `https://rpc.bdag-us.org` - US-based DAG RPC
- `https://rpc.dvdmining.com` - DVD Mining RPC

### JSON-RPC Methods Used

- `eth_getBalance(address, blockTag)` - Returns wallet balance in wei
- Balance is automatically converted to BDAG (1 BDAG = 10^18 wei)

## Dependencies

### Core
- Jetpack Compose 2023.10.01
- Material Design 3
- Navigation Compose

### Networking
- Retrofit 2.9.0
- OkHttp 4.11.0
- Gson 2.10.1

### Data & Security
- DataStore Preferences 1.0.0
- Android Security Crypto 1.1.0-alpha06
- Web3j 4.9.8 (for key cryptography)

### Architecture
- Hilt 2.48 (Dependency Injection)
- Coroutines 1.7.3
- ViewModel (Jetpack Lifecycle)

## Building for Release

### Key Store Setup
```bash
# Create a keystore file
keytool -genkey -v -keystore ~/welshdag-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias welshdag-key
```

### Build Release APK
```bash
./gradlew bundleRelease
# or for APK:
./gradlew assembleRelease -Pandroid.injected.signing.store.file=~/welshdag-release.jks \
  -Pandroid.injected.signing.store.password=yourpassword \
  -Pandroid.injected.signing.key.alias=welshdag-key \
  -Pandroid.injected.signing.key.password=yourpassword
```

## Troubleshooting

### Build Errors
- Ensure Android SDK 26+ is installed
- Update to latest Android Studio
- Run `./gradlew clean` before rebuilding

### Runtime Issues
- **"All RPC endpoints are offline"**: Check internet connection
- **"Invalid private key length"**: Private key must be 64 hex characters
- **Wallet import failing**: Ensure private key format is correct (with or without 0x prefix)

### RPC Connection Issues
- Check device internet connectivity
- Verify RPC endpoints are accessible
- Check app has INTERNET permission (should be automatic from manifest)

## Future Enhancements

- [ ] Token balance checking
- [ ] Transaction history
- [ ] Send BDAG feature
- [ ] QR code scanning for addresses
- [ ] Multiple wallet management
- [ ] Biometric authentication
- [ ] Ledger/Hardware wallet support
- [ ] Staking interface
- [ ] Push notifications for balance alerts

## Contributing

To add features or fix bugs:
1. Create a feature branch
2. Make changes
3. Test thoroughly on emulator and device
4. Submit pull request

## License

MIT License - See LICENSE file for details

## Support

For issues, questions, or suggestions:
- Report bugs on GitHub
- Check documentation at scan.welshdag.trade
- Contact: support@welshdag.trade

## Disclaimer

This app handles real cryptocurrency transactions. Always:
- Test on testnet first
- Verify wallet addresses
- Keep private keys secure
- Never share recovery information
- Use official releases only

---

Built with ❤️ for the WelshDAG community
