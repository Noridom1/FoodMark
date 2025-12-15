package com.example.foodmark

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FinalMobileApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: FinalMobileApp
            private set
    }
}