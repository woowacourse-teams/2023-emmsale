package com.emmsale.presentation

import android.app.Application
import com.emmsale.data.common.ServiceFactory
import com.emmsale.di.RepositoryContainer
import com.emmsale.di.ServiceContainer
import com.emmsale.di.SharedPreferenceContainer

class KerdyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        repositoryContainer = RepositoryContainer(
            serviceContainer = ServiceContainer(ServiceFactory()),
            preferenceContainer = SharedPreferenceContainer(this)
        )
    }

    companion object {
        lateinit var repositoryContainer: RepositoryContainer
            private set
    }
}
