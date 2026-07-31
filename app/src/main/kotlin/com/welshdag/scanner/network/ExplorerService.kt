package com.welshdag.scanner.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Blockscout v2 REST API as served by scan.welshdag.trade. No auth required. */
interface ExplorerService {

    @GET("api/v2/stats")
    suspend fun stats(): NetworkStats

    @GET("api/v2/main-page/blocks")
    suspend fun latestBlocks(): List<BlockSummary>

    @GET("api/v2/main-page/transactions")
    suspend fun latestTransactions(): List<TransactionSummary>

    @GET("api/v2/search")
    suspend fun search(@Query("q") query: String): SearchResponse

    @GET("api/v2/addresses/{hash}")
    suspend fun address(@Path("hash") hash: String): AddressDetail

    @GET("api/v2/addresses/{hash}/transactions")
    suspend fun addressTransactions(@Path("hash") hash: String): TransactionsPage
}
