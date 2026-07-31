package com.welshdag.scanner.network

import com.google.gson.annotations.SerializedName

data class JsonRpcRequest(
    val jsonrpc: String = "2.0",
    val method: String,
    val params: List<String>,
    val id: Int = 1
)

data class JsonRpcResponse<T>(
    val jsonrpc: String,
    val result: T?,
    val error: JsonRpcError?,
    val id: Int
)

data class JsonRpcError(
    val code: Int,
    val message: String
)

data class BalanceResult(
    @SerializedName("result")
    val balance: String
)

data class AccountBalance(
    val address: String,
    val balance: String,
    val rpcEndpoint: String,
    val isOnline: Boolean
)

data class WalletInfo(
    val address: String,
    val privateKey: String,
    val createdAt: Long = System.currentTimeMillis()
)
