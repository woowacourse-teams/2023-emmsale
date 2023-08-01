package com.emmsale.presentation.common.firebase.analytics

import com.emmsale.data.token.Token
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.ParametersBuilder
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

object Kerdy {
    private val firebaseAnalytics: FirebaseAnalytics by lazy { Firebase.analytics }

    fun initFirebaseAnalytics(token: Token) {
        firebaseAnalytics.setUserId(token.uid.toString())
    }

    fun log(event: String, parameters: ParametersBuilder.() -> Unit = {}) {
        firebaseAnalytics.logEvent(event, parameters)
    }
}

