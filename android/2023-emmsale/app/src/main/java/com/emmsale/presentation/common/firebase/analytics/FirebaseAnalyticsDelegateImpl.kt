package com.emmsale.presentation.common.firebase.analytics

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class FirebaseAnalyticsDelegateImpl(
    override val screenName: String,
) : FirebaseAnalyticsDelegate, DefaultLifecycleObserver {

    override fun registerScreen(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        logScreen(screenName)
    }
}
