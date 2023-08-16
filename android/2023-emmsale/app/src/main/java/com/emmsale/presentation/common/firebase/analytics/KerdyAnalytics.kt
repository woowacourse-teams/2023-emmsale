package com.emmsale.presentation.common.firebase.analytics

import com.emmsale.presentation.KerdyApplication
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.ParametersBuilder
import com.google.firebase.analytics.ktx.logEvent

fun log(event: String, parameters: ParametersBuilder.() -> Unit = {}) {
    KerdyApplication.firebaseAnalytics.logEvent(event, parameters)
}

fun logScreen(screenName: String) {
    log(FirebaseAnalytics.Event.SCREEN_VIEW) {
        param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
    }
}
