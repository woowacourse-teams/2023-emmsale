package com.emmsale.presentation

import android.app.Application
import com.emmsale.data.common.ServiceFactory
import com.emmsale.di.RepositoryContainer
import com.emmsale.di.ServiceContainer
import com.emmsale.di.SharedPreferenceContainer
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class KerdyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initDiContainer()
        initFirebaseAnalytics()
    }

    private fun initDiContainer() {
        repositoryContainer = RepositoryContainer(
            serviceContainer = ServiceContainer(ServiceFactory()),
            preferenceContainer = SharedPreferenceContainer(this),
        )
    }

    private fun initFirebaseAnalytics() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    companion object {
        lateinit var repositoryContainer: RepositoryContainer
            private set
        lateinit var firebaseAnalytics: FirebaseAnalytics
            private set
        val applicationScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    }
}
