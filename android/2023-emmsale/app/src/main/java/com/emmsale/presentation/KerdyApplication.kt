package com.emmsale.presentation

import android.app.Application
import com.emmsale.data.common.ServiceFactory
import com.emmsale.di.RepositoryContainer
import com.emmsale.di.ServiceContainer
import com.emmsale.di.SharedPreferenceContainer
import com.emmsale.presentation.common.firebase.analytics.Kerdy.initFirebaseAnalytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KerdyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        repositoryContainer = RepositoryContainer(
            serviceContainer = ServiceContainer(ServiceFactory()),
            preferenceContainer = SharedPreferenceContainer(this),
        )
        applicationScope.launch {
            repositoryContainer.tokenRepository.getToken()?.let(::initFirebaseAnalytics)
        }
    }

    companion object {
        lateinit var repositoryContainer: RepositoryContainer
            private set
        val applicationScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    }
}
