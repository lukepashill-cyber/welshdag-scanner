package com.welshdag.scanner.data

import com.welshdag.scanner.network.AddressDetail
import com.welshdag.scanner.network.BlockSummary
import com.welshdag.scanner.network.ExplorerService
import com.welshdag.scanner.network.NetworkStats
import com.welshdag.scanner.network.SearchResultItem
import com.welshdag.scanner.network.TransactionSummary
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

data class ExplorerOverview(
    val stats: NetworkStats?,
    val blocks: List<BlockSummary>,
    val transactions: List<TransactionSummary>
)

@Singleton
class ExplorerRepository @Inject constructor(
    private val service: ExplorerService
) {
    /**
     * The three home-screen panels load together. Any one of them failing degrades
     * that panel only — a stats outage shouldn't blank the block list.
     */
    suspend fun overview(): ExplorerOverview = coroutineScope {
        val stats = async { runCatching { service.stats() }.getOrNull() }
        val blocks = async { runCatching { service.latestBlocks() }.getOrDefault(emptyList()) }
        val txs = async { runCatching { service.latestTransactions() }.getOrDefault(emptyList()) }

        ExplorerOverview(
            stats = stats.await(),
            blocks = blocks.await().take(10),
            transactions = txs.await().take(10)
        )
    }

    suspend fun search(query: String): List<SearchResultItem> =
        service.search(query.trim()).items

    suspend fun address(hash: String): AddressDetail = service.address(hash)

    suspend fun addressTransactions(hash: String): List<TransactionSummary> =
        service.addressTransactions(hash).items
}
