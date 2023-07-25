package com.emmsale.presentation

import android.app.Application
import android.content.Intent
import com.emmsale.data.common.ServiceFactory
import com.emmsale.di.RepositoryContainer
import com.emmsale.di.ServiceContainer
import com.emmsale.presentation.ui.login.LoginActivity

class KerdyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        repositoryContainer = RepositoryContainer(
            context = this,
            serviceContainer = ServiceContainer(ServiceFactory())
        )
    }

    companion object {
        lateinit var repositoryContainer: RepositoryContainer
            private set
    }
}
