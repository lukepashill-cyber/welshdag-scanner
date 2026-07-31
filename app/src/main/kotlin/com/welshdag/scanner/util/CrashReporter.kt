package com.welshdag.scanner.util

import android.content.Context
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Captures uncaught exceptions to disk so the next launch can display them.
 * Debug builds on a phone are painful to diagnose without a cable; this puts
 * the stack trace on screen instead.
 */
object CrashReporter {

    private const val PREFS = "welshdag_crash"
    private const val KEY_TRACE = "last_trace"

    fun install(context: Context) {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching { store(context, thread, throwable) }
            previous?.uncaughtException(thread, throwable)
        }
    }

    private fun store(context: Context, thread: Thread, throwable: Throwable) {
        val writer = StringWriter()
        PrintWriter(writer).use { throwable.printStackTrace(it) }

        val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK).format(Date())
        val report = buildString {
            appendLine("WelshDAG Scanner crash")
            appendLine(stamp)
            appendLine("thread: ${thread.name}")
            appendLine()
            append(writer.toString())
        }

        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TRACE, report)
            .commit()
    }

    fun lastCrash(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_TRACE, null)

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(KEY_TRACE).apply()
    }
}
