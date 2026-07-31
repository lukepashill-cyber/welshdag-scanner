package com.welshdag.scanner.network

import retrofit2.http.Body
import retrofit2.http.POST

interface RpcService {
    @POST("/")
    suspend fun call(@Body request: JsonRpcRequest): JsonRpcResponse<String>
}
