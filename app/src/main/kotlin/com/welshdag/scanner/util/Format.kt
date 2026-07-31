package com.welshdag.scanner.util

import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException

private val WEI_PER_TOKEN: BigDecimal = BigDecimal.TEN.pow(18)

/**
 * Wei (decimal string) to a human token amount. Balances routinely exceed Long,
 * so this stays in BigInteger the whole way.
 */
fun formatWeiDecimal(wei: String?, scale: Int = 6): String {
    if (wei.isNullOrBlank()) return "0"
    return try {
        BigDecimal(BigInteger(wei))
            .divide(WEI_PER_TOKEN, scale, RoundingMode.DOWN)
            .stripTrailingZeros()
            .toPlainString()
    } catch (e: NumberFormatException) {
        "0"
    }
}

/** Same, but from a 0x-prefixed hex quantity as returned by eth_getBalance. */
fun formatWeiHex(hex: String?, scale: Int = 8): String {
    if (hex.isNullOrBlank()) return "0"
    return try {
        val stripped = hex.removePrefix("0x").ifEmpty { "0" }
        BigDecimal(BigInteger(stripped, 16))
            .divide(WEI_PER_TOKEN, scale, RoundingMode.DOWN)
            .stripTrailingZeros()
            .toPlainString()
    } catch (e: NumberFormatException) {
        "0"
    }
}

/** 0x1234…abcd — long hashes never fit on a phone. */
fun shortenHash(hash: String?, head: Int = 8, tail: Int = 6): String {
    if (hash.isNullOrBlank()) return "—"
    if (hash.length <= head + tail + 1) return hash
    return "${hash.take(head)}…${hash.takeLast(tail)}"
}

/** "3m ago" from a Blockscout ISO-8601 timestamp. */
fun relativeTime(timestamp: String?): String {
    if (timestamp.isNullOrBlank()) return ""
    return try {
        val elapsed = Duration.between(Instant.parse(timestamp), Instant.now())
        val seconds = elapsed.seconds
        when {
            seconds < 0 -> "just now"
            seconds < 60 -> "${seconds}s ago"
            seconds < 3600 -> "${seconds / 60}m ago"
            seconds < 86_400 -> "${seconds / 3600}h ago"
            else -> "${seconds / 86_400}d ago"
        }
    } catch (e: DateTimeParseException) {
        ""
    }
}

/** 16074709 -> "16,074,709" */
fun groupDigits(value: String?): String {
    if (value.isNullOrBlank()) return "—"
    return try {
        String.format("%,d", BigInteger(value))
    } catch (e: NumberFormatException) {
        value
    }
}

fun groupDigits(value: Long): String = String.format("%,d", value)
