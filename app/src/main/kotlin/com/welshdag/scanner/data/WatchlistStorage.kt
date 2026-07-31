package com.welshdag.scanner.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.welshdag.scanner.network.WatchedAddress
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The watchlist holds public addresses only — nothing secret, so plain
 * SharedPreferences is the right storage. The app is read-only against the
 * chain and never handles a private key.
 */
@Singleton
class WatchlistStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    private val gson = Gson()
    private val listType = object : TypeToken<List<WatchedAddress>>() {}.type

    suspend fun all(): List<WatchedAddress> = withContext(Dispatchers.IO) {
        val json = prefs.getString(KEY_ADDRESSES, null) ?: return@withContext emptyList()
        runCatching { gson.fromJson<List<WatchedAddress>>(json, listType) }
            .getOrNull()
            .orEmpty()
    }

    /** Adding an address already on the list is a no-op rather than a duplicate. */
    suspend fun add(address: String, label: String?): List<WatchedAddress> =
        withContext(Dispatchers.IO) {
            val normalised = address.trim()
            val current = all()
            if (current.any { it.address.equals(normalised, ignoreCase = true) }) {
                return@withContext current
            }
            val updated = current + WatchedAddress(
                address = normalised,
                label = label?.trim()?.takeIf { it.isNotEmpty() }
            )
            persist(updated)
            updated
        }

    suspend fun remove(address: String): List<WatchedAddress> = withContext(Dispatchers.IO) {
        val updated = all().filterNot { it.address.equals(address, ignoreCase = true) }
        persist(updated)
        updated
    }

    private fun persist(addresses: List<WatchedAddress>) {
        prefs.edit().putString(KEY_ADDRESSES, gson.toJson(addresses)).apply()
    }

    private companion object {
        const val PREFS_NAME = "welshdag_watchlist"
        const val KEY_ADDRESSES = "addresses"
    }
}
