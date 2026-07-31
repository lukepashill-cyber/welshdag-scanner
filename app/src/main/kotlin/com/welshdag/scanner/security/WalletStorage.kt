package com.welshdag.scanner.security

import android.content.Context
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.welshdag.scanner.di.dataStore
import com.welshdag.scanner.network.WalletInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletStorage @Inject constructor(
    private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        "wallet_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    suspend fun saveWallet(wallet: WalletInfo) {
        context.dataStore.edit { preferences ->
            preferences[WALLET_KEY] = gson.toJson(wallet)
        }
        encryptedPrefs.edit().putString(
            PRIVATE_KEY_KEY,
            wallet.privateKey
        ).apply()
    }

    fun getWalletFlow(): Flow<WalletInfo?> {
        return context.dataStore.data.map { preferences ->
            val walletJson = preferences[WALLET_KEY] ?: return@map null
            gson.fromJson(walletJson, WalletInfo::class.java)
        }
    }

    suspend fun getWallet(): WalletInfo? {
        val preferences = context.dataStore.data.let { it }
        val walletJson = preferences.map { it[WALLET_KEY] }.let { flow ->
            var result: String? = null
            flow.collect { result = it }
            result
        }
        return walletJson?.let { gson.fromJson(it, WalletInfo::class.java) }
    }

    suspend fun clearWallet() {
        context.dataStore.edit { preferences ->
            preferences.remove(WALLET_KEY)
        }
        encryptedPrefs.edit().remove(PRIVATE_KEY_KEY).apply()
    }

    companion object {
        private const val WALLET_KEY = "wallet_info"
        private const val PRIVATE_KEY_KEY = "private_key"
    }
}
