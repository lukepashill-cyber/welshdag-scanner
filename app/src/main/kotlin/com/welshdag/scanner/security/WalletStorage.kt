package com.welshdag.scanner.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.welshdag.scanner.network.WalletInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wallet persistence. Everything — address and private key alike — lives in
 * EncryptedSharedPreferences so the key is never written to disk in the clear.
 */
@Singleton
class WalletStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    suspend fun saveWallet(wallet: WalletInfo) = withContext(Dispatchers.IO) {
        prefs.edit()
            .putString(KEY_ADDRESS, wallet.address)
            .putString(KEY_PRIVATE_KEY, wallet.privateKey)
            .putLong(KEY_CREATED_AT, wallet.createdAt)
            .commit()
        Unit
    }

    suspend fun getWallet(): WalletInfo? = withContext(Dispatchers.IO) {
        val address = prefs.getString(KEY_ADDRESS, null) ?: return@withContext null
        val privateKey = prefs.getString(KEY_PRIVATE_KEY, null) ?: return@withContext null
        WalletInfo(
            address = address,
            privateKey = privateKey,
            createdAt = prefs.getLong(KEY_CREATED_AT, 0L)
        )
    }

    suspend fun clearWallet() = withContext(Dispatchers.IO) {
        prefs.edit().clear().commit()
        Unit
    }

    private companion object {
        const val PREFS_NAME = "welshdag_wallet"
        const val KEY_ADDRESS = "address"
        const val KEY_PRIVATE_KEY = "private_key"
        const val KEY_CREATED_AT = "created_at"
    }
}
