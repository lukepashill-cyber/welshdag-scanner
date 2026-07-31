package com.welshdag.scanner.di

import com.welshdag.scanner.network.ExplorerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val EXPLORER_BASE_URL = "https://scan.welshdag.trade/"

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    fun provideExplorerService(okHttpClient: OkHttpClient): ExplorerService =
        Retrofit.Builder()
            .baseUrl(EXPLORER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExplorerService::class.java)
}
