package com.emmsale.presentation

import android.app.Application
import com.emmsale.R
import com.emmsale.presentation.common.KerdyNotificationChannel
import com.google.firebase.analytics.FirebaseAnalytics
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class KerdyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initFirebaseAnalytics()
        initNotificationChannels()
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
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
