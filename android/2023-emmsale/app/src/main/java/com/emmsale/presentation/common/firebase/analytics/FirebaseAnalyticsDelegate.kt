package com.emmsale.presentation.common.firebase.analytics

import androidx.lifecycle.LifecycleOwner

interface FirebaseAnalyticsDelegate {
    val screenName: String

    fun registerScreen(lifecycleOwner: LifecycleOwner)
}
