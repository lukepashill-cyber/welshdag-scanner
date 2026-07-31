package com.welshdag.scanner

import android.app.Application
import com.welshdag.scanner.util.CrashReporter
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WelshDagApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashReporter.install(this)
    }
}
