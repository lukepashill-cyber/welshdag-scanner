package com.welshdag.scanner.data

import com.welshdag.scanner.network.AccountBalance
import com.welshdag.scanner.network.JsonRpcRequest
import com.welshdag.scanner.network.RpcService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RpcRepository @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private val services: Map<String, RpcService> = RPC_ENDPOINTS.associateWith { endpoint ->
        Retrofit.Builder()
            .baseUrl(endpoint)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RpcService::class.java)
    }

    /** Queries every endpoint in parallel; one failing endpoint never fails the others. */
    suspend fun getBalance(address: String): List<AccountBalance> = coroutineScope {
        services.map { (endpoint, service) ->
            async {
                try {
                    val response = service.call(
                        JsonRpcRequest(
                            method = "eth_getBalance",
                            params = listOf(address, "latest")
                        )
                    )

                    val error = response.error
                    if (error != null) {
                        AccountBalance(address, endpoint, isOnline = true, error = error.message)
                    } else {
                        val hex = response.result
                        if (hex == null) {
                            AccountBalance(address, endpoint, isOnline = true, error = "Empty result")
                        } else {
                            AccountBalance(
                                address = address,
                                rpcEndpoint = endpoint,
                                isOnline = true,
                                balance = formatWei(hex)
                            )
                        }
                    }
                } catch (e: Exception) {
                    AccountBalance(
                        address = address,
                        rpcEndpoint = endpoint,
                        isOnline = false,
                        error = e.message ?: e.javaClass.simpleName
                    )
                }
            }
        }.awaitAll()
    }

    /**
     * Wei is up to 2^256, so this must go through BigInteger — a Long overflows
     * at roughly 9.2 tokens' worth of wei.
     */
    private fun formatWei(hex: String): String {
        val wei = BigInteger(hex.removePrefix("0x").ifEmpty { "0" }, 16)
        return BigDecimal(wei)
            .divide(WEI_PER_TOKEN, 8, RoundingMode.DOWN)
            .stripTrailingZeros()
            .toPlainString()
    }

    private companion object {
        val WEI_PER_TOKEN: BigDecimal = BigDecimal.TEN.pow(18)

        // Trailing slash is required — Retrofit rejects a baseUrl without one.
        val RPC_ENDPOINTS = listOf(
            "https://rpc.welshdag.trade/",
            "https://rpc.capedag.com/",
            "https://rpc.bdag-us.org/",
            "https://rpc.dvdmining.com/"
        )
    }
}
