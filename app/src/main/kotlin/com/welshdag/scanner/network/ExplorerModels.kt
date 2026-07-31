package com.welshdag.scanner.network

import com.google.gson.annotations.SerializedName

/**
 * Blockscout v2 API models. Only the fields the UI actually renders are mapped;
 * Gson ignores the rest. Almost everything is nullable — Blockscout omits fields
 * freely depending on chain features, and a missing field must not crash a screen.
 */

data class NetworkStats(
    @SerializedName("total_blocks") val totalBlocks: String? = null,
    @SerializedName("total_transactions") val totalTransactions: String? = null,
    @SerializedName("total_addresses") val totalAddresses: String? = null,
    @SerializedName("average_block_time") val averageBlockTimeMs: Double? = null,
    @SerializedName("transactions_today") val transactionsToday: String? = null,
    @SerializedName("gas_used_today") val gasUsedToday: String? = null,
    @SerializedName("network_utilization_percentage") val networkUtilization: Double? = null
)

data class AddressRef(
    val hash: String,
    val name: String? = null,
    @SerializedName("is_contract") val isContract: Boolean? = null
)

data class BlockSummary(
    val height: Long,
    val hash: String,
    val timestamp: String? = null,
    @SerializedName("transactions_count") val transactionCount: Int? = null,
    @SerializedName("gas_used") val gasUsed: String? = null,
    val size: Long? = null,
    val miner: AddressRef? = null
)

data class BlocksPage(
    val items: List<BlockSummary> = emptyList()
)

data class TransactionSummary(
    val hash: String,
    val value: String? = null,
    val timestamp: String? = null,
    val status: String? = null,
    val result: String? = null,
    @SerializedName("block_number") val blockNumber: Long? = null,
    @SerializedName("from") val fromAddress: AddressRef? = null,
    @SerializedName("to") val toAddress: AddressRef? = null,
    val fee: TransactionFee? = null,
    val method: String? = null
)

data class TransactionFee(
    val value: String? = null
)

data class TransactionsPage(
    val items: List<TransactionSummary> = emptyList()
)

data class AddressDetail(
    val hash: String,
    @SerializedName("coin_balance") val coinBalance: String? = null,
    @SerializedName("is_contract") val isContract: Boolean? = null,
    @SerializedName("block_number_balance_updated_at") val balanceUpdatedAtBlock: Long? = null,
    val name: String? = null
)

data class SearchResultItem(
    val type: String? = null,
    @SerializedName("address_hash") val addressHash: String? = null,
    @SerializedName("transaction_hash") val transactionHash: String? = null,
    @SerializedName("block_hash") val blockHash: String? = null,
    @SerializedName("block_number") val blockNumber: Long? = null,
    val name: String? = null,
    val url: String? = null
)

data class SearchResponse(
    val items: List<SearchResultItem> = emptyList()
)
