package com.welshdag.scanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.welshdag.scanner.data.RpcRepository
import com.welshdag.scanner.network.AccountBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val rpcRepository: RpcRepository
) : ViewModel() {

    private val _balances = MutableStateFlow<List<AccountBalance>>(emptyList())
    val balances: StateFlow<List<AccountBalance>> = _balances.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchBalances(address: String) {
        if (address.isBlank()) {
            _errorMessage.value = "Invalid address"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val results = rpcRepository.getBalance(address)
                _balances.value = results
                if (results.all { !it.isOnline }) {
                    _errorMessage.value = "All RPC endpoints are offline"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching balances: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshBalances(address: String) {
        fetchBalances(address)
    }
}
