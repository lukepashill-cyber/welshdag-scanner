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

/**
 * A public address the user has chosen to follow. Deliberately holds no key
 * material — the app only ever reads from the chain.
 */
data class WatchedAddress(
    val address: String,
    val label: String? = null,
    val addedAt: Long = System.currentTimeMillis()
)

/** 0x followed by 40 hex characters. */
fun isValidAddress(input: String): Boolean {
    val trimmed = input.trim()
    if (!trimmed.startsWith("0x") && !trimmed.startsWith("0X")) return false
    val body = trimmed.drop(2)
    return body.length == 40 && body.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
}
