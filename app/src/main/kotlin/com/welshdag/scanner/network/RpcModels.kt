package com.welshdag.scanner.network

data class JsonRpcRequest(
    val jsonrpc: String = "2.0",
    val method: String,
    val params: List<String>,
    val id: Int = 1
)

data class JsonRpcResponse<T>(
    val jsonrpc: String? = null,
    val result: T? = null,
    val error: JsonRpcError? = null,
    val id: Int = 0
)

data class JsonRpcError(
    val code: Int,
    val message: String
)

/**
 * One endpoint's answer for one address. [balance] is null whenever [error] is set.
 */
data class AccountBalance(
    val address: String,
    val rpcEndpoint: String,
    val isOnline: Boolean,
    val balance: String? = null,
    val error: String? = null
) {
    val host: String
        get() = rpcEndpoint.removePrefix("https://").removePrefix("http://").trimEnd('/')
}

data class WalletInfo(
    val address: String,
    val privateKey: String,
    val createdAt: Long = System.currentTimeMillis()
)
