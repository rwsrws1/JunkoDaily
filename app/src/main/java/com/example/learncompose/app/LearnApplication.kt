package com.example.learncompose.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LearnApplication : Application() {
    override fun onCreate() {
        super.onCreate()

    }
}