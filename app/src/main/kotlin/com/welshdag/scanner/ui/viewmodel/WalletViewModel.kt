package com.welshdag.scanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.welshdag.scanner.network.WalletInfo
import com.welshdag.scanner.security.WalletStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletStorage: WalletStorage
) : ViewModel() {

    private val _walletState = MutableStateFlow<WalletState>(WalletState.Loading)
    val walletState: StateFlow<WalletState> = _walletState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            val wallet = walletStorage.getWallet()
            _walletState.value = wallet?.let { WalletState.Connected(it) } ?: WalletState.Empty
        }
    }

    fun generateNewWallet() {
        viewModelScope.launch {
            try {
                // Key generation is CPU-bound; keep it off the main thread.
                val wallet = withContext(Dispatchers.Default) {
                    val keyPair = Keys.createEcKeyPair()
                    WalletInfo(
                        address = "0x${Keys.getAddress(keyPair)}",
                        privateKey = keyPair.privateKey.toString(16).padStart(64, '0')
                    )
                }
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
            val cleanKey = privateKeyHex.trim().removePrefix("0x").removePrefix("0X")

            if (cleanKey.length != 64) {
                _errorMessage.value =
                    "A private key is 64 hex characters — this one has ${cleanKey.length}."
                return@launch
            }
            if (!cleanKey.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }) {
                _errorMessage.value = "Private key contains non-hexadecimal characters."
                return@launch
            }

            try {
                val wallet = withContext(Dispatchers.Default) {
                    val keyPair = ECKeyPair.create(BigInteger(cleanKey, 16))
                    WalletInfo(
                        address = "0x${Keys.getAddress(keyPair)}",
                        privateKey = cleanKey
                    )
                }
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

    sealed interface WalletState {
        data object Loading : WalletState
        data object Empty : WalletState
        data class Connected(val wallet: WalletInfo) : WalletState
    }
}
