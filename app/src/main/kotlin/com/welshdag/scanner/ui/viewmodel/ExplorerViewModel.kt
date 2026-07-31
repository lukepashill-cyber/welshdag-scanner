package com.welshdag.scanner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.welshdag.scanner.data.ExplorerOverview
import com.welshdag.scanner.data.ExplorerRepository
import com.welshdag.scanner.network.SearchResultItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExplorerViewModel @Inject constructor(
    private val repository: ExplorerRepository
) : ViewModel() {

    private val _overview = MutableStateFlow<ExplorerOverview?>(null)
    val overview: StateFlow<ExplorerOverview?> = _overview.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResultItem>?>(null)
    val searchResults: StateFlow<List<SearchResultItem>?> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var refreshJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (refreshJob?.isActive == true) return
        refreshJob = viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = repository.overview()
                _overview.value = result
                if (result.stats == null && result.blocks.isEmpty() && result.transactions.isEmpty()) {
                    _errorMessage.value = "Could not reach scan.welshdag.trade."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load explorer data."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun search(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            clearSearch()
            return
        }
        viewModelScope.launch {
            _isSearching.value = true
            try {
                _searchResults.value = repository.search(trimmed)
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                _errorMessage.value = "Search failed: ${e.message}"
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun clearSearch() {
        _searchResults.value = null
    }
}
