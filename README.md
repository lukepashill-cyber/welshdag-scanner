# WelshDAG Scanner

Android app for checking a wallet balance across the WelshDAG public RPC endpoints.

## Status

**Not yet verified.** This code has not been run on a device. It is being built via
GitHub Actions; see the Actions tab for whether the current commit compiles. Treat
every claim below as intent, not as tested behaviour, until a build is green and the
APK has been exercised on real hardware.

## What it does

- Generate a new secp256k1 wallet, or import an existing private key
- Store the key in `EncryptedSharedPreferences` (AES-256-GCM, hardware-backed master key)
- Query `eth_getBalance` against four endpoints in parallel, showing each one's result
  or its error independently:
  - `rpc.welshdag.trade`
  - `rpc.capedag.com`
  - `rpc.bdag-us.org`
  - `rpc.dvdmining.com`

## Getting the APK

Every push runs `.github/workflows/build.yml`. When it succeeds, the APK is attached
to the run as an artifact named `welshdag-scanner-debug` — download it from the
Actions tab, unzip, and install:

```
adb install -r app-debug.apk
```

## Building locally

Requires JDK 17 and the Android SDK (API 34).

```
gradle assembleDebug
```

Output lands in `app/build/outputs/apk/debug/`.

There is no Gradle wrapper committed. Either use a locally installed Gradle 8.4+, or
run `gradle wrapper` once to generate one.

## Security notes

- The private key is written only to `EncryptedSharedPreferences` and is excluded from
  cloud backup and device transfer (see `res/xml/`) — the ciphertext is bound to this
  device's keystore and would not be readable elsewhere anyway.
- The app is read-only against the chain. It signs nothing and sends no transactions,
  so a compromised endpoint can at worst report a wrong balance.
- Debug builds are unsigned for release purposes and should not be distributed.

## Layout

```
app/src/main/kotlin/com/welshdag/scanner/
├── MainActivity.kt          navigation host
├── WelshDagApp.kt           Hilt entry point
├── data/RpcRepository.kt    parallel balance queries
├── di/NetworkModule.kt      OkHttp provider
├── network/                 JSON-RPC models + Retrofit interface
├── security/WalletStorage.kt
└── ui/                      screens, theme, view models
```
