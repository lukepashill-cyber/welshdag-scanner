package com.welshdag.scanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.welshdag.scanner.data.ExplorerRepository
import com.welshdag.scanner.data.RpcRepository
import com.welshdag.scanner.network.AccountBalance
import com.welshdag.scanner.network.AddressDetail
import com.welshdag.scanner.network.TransactionSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * One address, seen two ways: what the explorer says about it, and what each
 * peer RPC node independently reports for its balance.
 */
@HiltViewModel
class AddressViewModel @Inject constructor(
    private val explorerRepository: ExplorerRepository,
    private val rpcRepository: RpcRepository
) : ViewModel() {

    private val _detail = MutableStateFlow<AddressDetail?>(null)
    val detail: StateFlow<AddressDetail?> = _detail.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionSummary>>(emptyList())
    val transactions: StateFlow<List<TransactionSummary>> = _transactions.asStateFlow()

    private val _peerBalances = MutableStateFlow<List<AccountBalance>>(emptyList())
    val peerBalances: StateFlow<List<AccountBalance>> = _peerBalances.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var loadJob: Job? = null

    fun load(address: String) {
        if (address.isBlank()) {
            _errorMessage.value = "No address supplied."
            return
        }
        if (loadJob?.isActive == true) return

        loadJob = viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                coroutineScope {
                    val detail = async {
                        runCatching { explorerRepository.address(address) }.getOrNull()
                    }
                    val txs = async {
                        runCatching { explorerRepository.addressTransactions(address) }
                            .getOrDefault(emptyList())
                    }
                    val peers = async { rpcRepository.getBalance(address) }

                    _detail.value = detail.await()
                    _transactions.value = txs.await()
                    _peerBalances.value = peers.await()
                }

                if (_detail.value == null && _peerBalances.value.none { it.error == null }) {
                    _errorMessage.value = "Neither the explorer nor any peer node responded."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load address."
            } finally {
                _isLoading.value = false
            }
        }
    }
}
