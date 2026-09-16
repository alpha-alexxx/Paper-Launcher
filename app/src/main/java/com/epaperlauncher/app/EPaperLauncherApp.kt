package com.epaperlauncher.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class EPaperLauncherApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
