package com.welshdag.scanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.welshdag.scanner.data.WatchlistStorage
import com.welshdag.scanner.network.WatchedAddress
import com.welshdag.scanner.network.isValidAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val storage: WatchlistStorage
) : ViewModel() {

    private val _addresses = MutableStateFlow<List<WatchedAddress>>(emptyList())
    val addresses: StateFlow<List<WatchedAddress>> = _addresses.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            _addresses.value = storage.all()
            _isLoading.value = false
        }
    }

    fun add(address: String, label: String) {
        val trimmed = address.trim()

        if (trimmed.isEmpty()) {
            _errorMessage.value = "Enter an address."
            return
        }
        if (!isValidAddress(trimmed)) {
            _errorMessage.value =
                "That doesn't look like an address — expected 0x followed by 40 hex characters."
            return
        }
        if (_addresses.value.any { it.address.equals(trimmed, ignoreCase = true) }) {
            _errorMessage.value = "That address is already on your list."
            return
        }

        viewModelScope.launch {
            _addresses.value = storage.add(trimmed, label)
            _errorMessage.value = null
        }
    }

    fun remove(address: String) {
        viewModelScope.launch {
            _addresses.value = storage.remove(address)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
