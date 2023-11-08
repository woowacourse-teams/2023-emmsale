package com.emmsale.presentation

import android.app.Application
import com.emmsale.presentation.common.KerdyNotificationChannel
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KerdyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initFirebaseAnalytics()
        initNotificationChannels()
    }

    private fun initFirebaseAnalytics() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    private fun initNotificationChannels() {
        KerdyNotificationChannel.createChannels(this)
    }

    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
            private set
    }
}
