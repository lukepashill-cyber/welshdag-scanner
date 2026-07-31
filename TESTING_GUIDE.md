# WelshDAG Scanner - Testing Guide

## Pre-Testing Checklist

- [ ] App builds successfully: `./gradlew build`
- [ ] No Gradle errors
- [ ] All Kotlin files are in correct package
- [ ] AndroidManifest.xml has internet permission
- [ ] Device/emulator connected
- [ ] Internet connection available

## Manual Testing Scenarios

### 1. App Launch Test

**Steps:**
1. Run app: `./gradlew installDebug`
2. Look for "WelshDAG Scanner" home screen
3. Verify both buttons are visible

**Expected Results:**
- ✅ App launches without crashing
- ✅ Home screen displays with title and subtitle
- ✅ Two buttons visible: "Connect Wallet" and "Import Wallet"
- ✅ Dark/light theme based on system settings

---

### 2. Wallet Generation Test

**Steps:**
1. From Home screen, click "Connect Wallet"
2. Click "Generate New Wallet"
3. Wait for wallet to generate
4. Verify address is shown

**Expected Results:**
- ✅ No crash during generation
- ✅ Address displays in format `0x...`
- ✅ Address is 42 characters long (0x + 40 hex chars)
- ✅ "Check Balance" and "Disconnect" buttons appear
- ✅ Button states work correctly

**Common Issues:**
- Address not showing → Check WalletViewModel logs
- Crash on generation → Verify Web3j dependency
- Invalid address format → Check Keys.getAddress() call

---

### 3. Private Key Import Test

**Steps:**
1. Go to Home screen
2. Click "Connect Wallet"
3. Click "Import Private Key"
4. Enter a valid private key (64 hex characters, with or without 0x)
5. Click "Import"

**Test Cases:**

**Valid Private Key:**
```
Paste: c9b49b877c5e2c8b3c9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a
```
**Expected:** Wallet connects successfully

**Key with 0x prefix:**
```
Paste: 0xc9b49b877c5e2c8b3c9d8e7f6a5b4c3d2e1f0a9b8c7d6e5f4a3b2c1d0e9f8a
```
**Expected:** Works (0x prefix stripped)

**Invalid Key (too short):**
```
Paste: abcdef123456
```
**Expected:** Error message "Invalid private key length"

**Invalid Key (non-hex):**
```
Paste: gggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg
```
**Expected:** Error message about invalid private key

**Results:**
- ✅ Valid key imports successfully
- ✅ Address displays after import
- ✅ Invalid keys show error messages
- ✅ Error messages are user-friendly
- ✅ No crash on invalid input

---

### 4. Balance Checking Test

**Steps:**
1. After wallet connection, click "Check Balance"
2. App should query 4 RPC endpoints
3. Wait for results to load
4. Verify balance card displays

**Expected Results:**
- ✅ Loading indicator shows while fetching
- ✅ 4 balance cards appear (one per RPC endpoint)
- ✅ Each card shows:
  - RPC endpoint name
  - Online/Offline status
  - Balance in BDAG
- ✅ At least one endpoint is online
- ✅ Balances are consistent (or show error)

**RPC Endpoints to Check:**
- rpc.welshdag.trade
- rpc.capedag.com
- rpc.bdag-us.org
- rpc.dvdmining.com

---

### 5. Refresh Button Test

**Steps:**
1. On Balance screen, click "Refresh" button
2. Wait for new query
3. Compare results with previous

**Expected Results:**
- ✅ Loading indicator appears
- ✅ Refresh button is disabled during load
- ✅ New balances load
- ✅ Results may match previous (expected for stable blockchain)

---

### 6. Navigation Test

**Steps:**
1. Home → Connect Wallet → Back → Home (verify back button works)
2. Home → Connect Wallet → Generate Wallet → Check Balance → Back
3. Home → Connect Wallet → Generate Wallet → Check Balance → Refresh → Back → Back → Home
4. Test disconnect and reconnect flow

**Expected Results:**
- ✅ Back button always returns to previous screen
- ✅ Navigation stack works correctly
- ✅ No data loss on navigation
- ✅ State persists appropriately

---

### 7. Persistence Test

**Steps:**
1. Generate/import wallet
2. Go to balance screen
3. Close app (force stop in settings)
4. Reopen app

**Expected Results:**
- ✅ Wallet is still connected
- ✅ Address is the same
- ✅ No need to re-import
- ✅ Can immediately check balance

---

### 8. Theme Test

**Steps:**
1. Go to Settings → Display → Theme
2. Switch between Light/Dark theme
3. Return to app

**Expected Results:**
- ✅ UI colors change appropriately
- ✅ Text remains readable
- ✅ All screens respect theme
- ✅ No colors are hard-coded

---

### 9. Network Resilience Test

**Steps:**
1. Turn off internet
2. Try to check balance
3. Verify error handling
4. Turn internet back on
5. Refresh balance

**Expected Results:**
- ✅ App shows "All RPC endpoints are offline" or error message
- ✅ No crash
- ✅ Clear error message
- ✅ Refresh works after internet restored

---

### 10. Input Validation Tests

**Private Key Length Validation:**
```
Input: Too short (< 64 chars)
Expected: "Invalid private key length"

Input: Too long (> 64 chars)
Expected: "Invalid private key length"

Input: Exactly 64 hex chars
Expected: Success
```

**Address Validation:**
```
Input: Empty address
Expected: "Invalid address"

Input: Valid address
Expected: Balance displayed
```

---

## UI/UX Testing

### Screen Transitions
- [ ] All screens render without glitches
- [ ] Text is legible on all screens
- [ ] Buttons are tappable (>48dp)
- [ ] No overlapping elements

### Responsive Design
- [ ] App works on small screens (4")
- [ ] App works on large screens (7"+)
- [ ] Landscape mode doesn't break UI
- [ ] Text sizes are appropriate

### Accessibility
- [ ] All buttons have descriptive labels
- [ ] Colors have sufficient contrast
- [ ] Text is readable (minimum 14sp)
- [ ] Interactive elements are properly sized

---

## Performance Testing

### Memory Usage
```bash
# Monitor during balance check
adb shell dumpsys meminfo com.welshdag.scanner
```
- [ ] No memory leaks
- [ ] Memory < 100MB typical
- [ ] Memory < 200MB peak

### Network Performance
- [ ] Balance queries complete < 10 seconds
- [ ] RPC endpoints respond in < 5 seconds each
- [ ] App handles timeouts gracefully

### App Responsiveness
- [ ] UI remains responsive during RPC calls
- [ ] Buttons don't freeze
- [ ] Scrolling is smooth
- [ ] Animations are fluid

---

## Edge Cases

### Address Validation
- [ ] Checksum addresses work
- [ ] Lowercase addresses work
- [ ] Uppercase addresses work
- [ ] Mixed case addresses work
- [ ] Addresses with 0x prefix work
- [ ] Addresses without 0x prefix work

### Balance Display
- [ ] Balance shows correctly for 0 BDAG
- [ ] Balance shows correctly for very large amounts
- [ ] Balance shows correctly for decimals
- [ ] Scientific notation is handled

### RPC Failures
- [ ] Handles 1 RPC endpoint offline
- [ ] Handles 2 RPC endpoints offline
- [ ] Handles 3 RPC endpoints offline
- [ ] Handles all 4 endpoints offline

---

## Security Testing

### Private Key Security
- [ ] Private key never appears in logs
- [ ] Private key not visible on screen
- [ ] Private key survives app restart
- [ ] Private key is encrypted in storage

### Network Security
- [ ] All RPC calls use HTTPS
- [ ] Certificate validation is active
- [ ] No sensitive data in logs
- [ ] No API keys in code

---

## Crash Testing

### Force Stop & Resume
1. Start balance check
2. Force stop app via settings
3. Reopen app
4. **Expected:** No crash, clean state

### Memory Pressure
1. Open app
2. Open 10+ other apps to create memory pressure
3. Return to scanner app
4. **Expected:** App survives or recovers gracefully

### Airplane Mode Toggle
1. Check balance
2. Toggle airplane mode on mid-check
3. Toggle airplane mode off
4. **Expected:** Proper error handling

---

## Test Results Template

```
TEST RUN: [Date]
Device: [Model, Android Version]
Build: [Build Number]

✅ PASS / ❌ FAIL - Test Case Name
- Issue: [If failed, describe]
- Notes: [Any observations]

SUMMARY:
- Total Tests: X
- Passed: X
- Failed: X
- Not Tested: X
```

---

## Automated Testing (Optional)

### Unit Tests
```kotlin
// Example for WalletViewModel
@Test
fun testGenerateWallet() {
    viewModel.generateNewWallet()
    // Assert wallet state changed
}
```

### Integration Tests
```kotlin
// Test RPC calls
@Test
suspend fun testBalanceQuery() {
    val balance = repository.getBalance("0x...")
    assertNotNull(balance)
}
```

Run tests:
```bash
./gradlew test                  # Unit tests
./gradlew connectedAndroidTest  # Instrumentation tests
```

---

## Regression Testing

After each change, test:
1. App still launches
2. Wallet generation works
3. Private key import works
4. Balance checking works
5. Navigation is intact
6. No new crashes

---

## Release Checklist

Before release build:
- [ ] All tests pass
- [ ] No known bugs
- [ ] ProGuard working
- [ ] Build is release variant
- [ ] Version bumped
- [ ] Signed with keystore
- [ ] No debug logging

---

## Troubleshooting Test Failures

| Failure | Likely Cause | Fix |
|---------|--------------|-----|
| App crashes on launch | Missing dependency | Check Gradle sync |
| RPC always offline | Network issue | Check internet connection |
| Balance always zero | Address issue | Verify with known wallet |
| Private key import fails | Key format | Ensure 64 hex chars |
| UI looks broken | Theme issue | Check Theme.kt |
| Wallet doesn't persist | Storage issue | Verify WalletStorage |

---

## Performance Baseline

Target metrics (measured during testing):
- App startup: < 3 seconds
- Balance query: < 10 seconds
- Memory usage: < 100MB typical
- Battery drain: < 2% per hour idle
- Data usage: < 1MB per query

---

## Sign-Off

Testing Date: ___________
Tester Name: ___________
Build Tested: ___________

Overall Status: ✅ PASS / ❌ FAIL

Known Issues:
1. ___________
2. ___________
3. ___________

Ready for Release: ✅ YES / ❌ NO

---

Good luck with testing! 🚀
