# WelshDAG Scanner

Android client for the WelshDAG chain: a mobile front end for the
[scan.welshdag.trade](https://scan.welshdag.trade) block explorer, plus a
watchlist of addresses whose balances you can compare across the public peer
nodes.

## Watch-only by design

**The app never handles a private key.** Everything it does is read-only, so it
takes a public address and nothing else. There is no key generation, no import,
no signing, and no `web3j` dependency. Losing the phone leaks nothing beyond a
list of addresses that were already publicly viewable.

## Features

**Explorer** — served by the Blockscout v2 API at `scan.welshdag.trade`
- Network stats: total blocks, transactions, addresses, average block time
- Latest blocks and latest transactions
- Search across addresses, transactions and blocks

**My addresses**
- Add any address, optionally labelled ("mining payouts", "cold wallet")
- Stored locally in `SharedPreferences`; nothing leaves the device
- Tap through to the address view

**Address view** — the two sources side by side
- The explorer's balance and full transaction history, marked IN or OUT
- Each peer node's independent `eth_getBalance` for the same address:
  - `rpc.welshdag.trade`
  - `rpc.capedag.com`
  - `rpc.bdag-us.org`
  - `rpc.dvdmining.com`
  - `rpc.bdagscan.com`

  Disagreement between those rows is itself informative — it means the nodes do
  not share a view of the chain.

## Status

Compiles and packages via CI. Runtime behaviour on a real device has not been
verified — treat anything beyond "the APK installs" as unconfirmed until tried.

## Getting the APK

Every push runs `.github/workflows/build.yml`. On success the APK is attached to
the run as artifact `welshdag-scanner-debug` — download it from the Actions tab,
unzip, then:

```
adb install -r app-debug.apk
```

## Building locally

Requires JDK 17 and the Android SDK (API 34).

```
gradle assembleDebug
```

No Gradle wrapper is committed; use a local Gradle 8.4+, or run `gradle wrapper`
once to generate one.

## Layout

```
app/src/main/kotlin/com/welshdag/scanner/
├── MainActivity.kt              two-tab navigation host
├── WelshDagApp.kt               Hilt entry point
├── data/
│   ├── ExplorerRepository.kt    Blockscout queries
│   ├── RpcRepository.kt         parallel peer-node balances
│   └── WatchlistStorage.kt      saved addresses
├── network/                     API models and Retrofit interfaces
├── ui/screens/                  Explorer, Watchlist, Address
├── ui/viewmodel/
└── util/Format.kt               wei, hashes, timestamps
```
