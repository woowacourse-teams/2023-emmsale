package com.emmsale.presentation

import android.app.Application
import android.content.Context
import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.activity.ActivityRepositoryImpl
import com.emmsale.data.activity.ActivityService
import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.login.LoginRepository
import com.emmsale.data.login.LoginRepositoryImpl
import com.emmsale.data.login.LoginService
import com.emmsale.data.token.TokenRepository
import com.emmsale.data.token.TokenRepositoryImpl
import com.emmsale.presentation.common.keys.KERDY_PREF_KEY

class KerdyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        repositoryContainer = RepositoryContainer(
            context = applicationContext,
            serviceContainer = ServiceContainer(ServiceFactory())
        )
    }

    companion object {
        lateinit var repositoryContainer: RepositoryContainer
            private set
    }
}

class RepositoryContainer(
    context: Context,
    serviceContainer: ServiceContainer,
) {
    val loginRepository: LoginRepository by lazy {
        LoginRepositoryImpl(loginService = serviceContainer.loginService)
    }
    val tokenRepository: TokenRepository by lazy {
        TokenRepositoryImpl(
            preference = context.getSharedPreferences(KERDY_PREF_KEY, Context.MODE_PRIVATE)
        )
    }
    val activityRepository: ActivityRepository by lazy {
        ActivityRepositoryImpl(activityService = serviceContainer.activityService)
    }
}

class ServiceContainer(serviceFactory: ServiceFactory) {
    val loginService: LoginService by lazy { serviceFactory.create(LoginService::class.java) }
    val activityService: ActivityService by lazy { serviceFactory.create(ActivityService::class.java) }
}
