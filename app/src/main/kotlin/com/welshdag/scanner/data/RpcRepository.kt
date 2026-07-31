package com.welshdag.scanner.data

import com.welshdag.scanner.network.AccountBalance
import com.welshdag.scanner.network.JsonRpcRequest
import com.welshdag.scanner.network.RpcService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RpcRepository @Inject constructor(
    private val primaryRpcService: RpcService
) {
    private val rpcEndpoints = listOf(
        "https://rpc.welshdag.trade",
        "https://rpc.capedag.com",
        "https://rpc.bdag-us.org",
        "https://rpc.dvdmining.com"
    )

    suspend fun getBalance(address: String): List<AccountBalance> = coroutineScope {
        val balances = rpcEndpoints.map { endpoint ->
            async {
                try {
                    val retrofit = Retrofit.Builder()
                        .baseUrl(endpoint)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val service = retrofit.create(RpcService::class.java)

                    val request = JsonRpcRequest(
                        method = "eth_getBalance",
                        params = listOf(address, "latest")
                    )
                    val response = service.call(request)

                    val balanceHex = response.result ?: "0x0"
                    val balanceWei = balanceHex.removePrefix("0x").toLong(16)
                    val balanceEth = balanceWei.toBigDecimal().divide(
                        "1000000000000000000".toBigDecimal()
                    )

                    AccountBalance(
                        address = address,
                        balance = balanceEth.toPlainString(),
                        rpcEndpoint = endpoint,
                        isOnline = true
                    )
                } catch (e: Exception) {
                    AccountBalance(
                        address = address,
                        balance = "Error",
                        rpcEndpoint = endpoint,
                        isOnline = false
                    )
                }
            }
        }
        balances.awaitAll()
    }
}
