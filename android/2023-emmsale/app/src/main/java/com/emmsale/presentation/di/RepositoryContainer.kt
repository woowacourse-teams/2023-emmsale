package com.emmsale.presentation.di

import android.content.Context
import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.activity.ActivityRepositoryImpl
import com.emmsale.data.login.LoginRepository
import com.emmsale.data.login.LoginRepositoryImpl
import com.emmsale.data.token.TokenRepository
import com.emmsale.data.token.TokenRepositoryImpl
import com.emmsale.presentation.common.keys.KERDY_PREF_KEY

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
