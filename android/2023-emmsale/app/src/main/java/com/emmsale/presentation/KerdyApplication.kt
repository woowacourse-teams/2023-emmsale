package com.emmsale.presentation

import android.app.Application
import android.app.NotificationManager
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
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val kerdyNotificationChannels = KerdyNotificationChannel.createChannels(this)
        notificationManager.createNotificationChannels(kerdyNotificationChannels)
    }

    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
            private set
    }
}
