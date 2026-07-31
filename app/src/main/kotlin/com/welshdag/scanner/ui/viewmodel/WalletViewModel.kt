package com.welshdag.scanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.welshdag.scanner.network.WalletInfo
import com.welshdag.scanner.security.WalletStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletStorage: WalletStorage
) : ViewModel() {

    private val _walletState = MutableStateFlow<WalletState>(WalletState.Empty)
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadWallet()
    }

    private fun loadWallet() {
        viewModelScope.launch {
            val wallet = walletStorage.getWallet()
            _walletState.value = if (wallet != null) {
                WalletState.Connected(wallet)
            } else {
                WalletState.Empty
            }
        }
    }

    fun generateNewWallet() {
        viewModelScope.launch {
            try {
                val keyPair = Keys.createEcKeyPair()
                val address = Keys.getAddress(keyPair)
                val privateKey = keyPair.privateKey.toString(16).padStart(64, '0')

                val wallet = WalletInfo(
                    address = "0x$address",
                    privateKey = privateKey
                )

                walletStorage.saveWallet(wallet)
                _walletState.value = WalletState.Connected(wallet)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to generate wallet: ${e.message}"
            }
        }
    }

    fun importPrivateKey(privateKeyHex: String) {
        viewModelScope.launch {
            try {
                val cleanKey = privateKeyHex.removePrefix("0x")
                if (cleanKey.length != 64) {
                    _errorMessage.value = "Invalid private key length"
                    return@launch
                }

                val privateKeyBigInt = BigInteger(cleanKey, 16)
                val keyPair = ECKeyPair.create(privateKeyBigInt)
                val address = Keys.getAddress(keyPair)

                val wallet = WalletInfo(
                    address = "0x$address",
                    privateKey = cleanKey
                )

                walletStorage.saveWallet(wallet)
                _walletState.value = WalletState.Connected(wallet)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to import private key: ${e.message}"
            }
        }
    }

    fun disconnectWallet() {
        viewModelScope.launch {
            walletStorage.clearWallet()
            _walletState.value = WalletState.Empty
            _errorMessage.value = null
        }
    }

    sealed class WalletState {
        object Empty : WalletState()
        data class Connected(val wallet: WalletInfo) : WalletState()
    }
}
