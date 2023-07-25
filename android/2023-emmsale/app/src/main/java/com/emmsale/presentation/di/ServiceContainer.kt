package com.emmsale.presentation.di

import com.emmsale.data.activity.ActivityService
import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.login.LoginService

class ServiceContainer(serviceFactory: ServiceFactory) {
    val loginService: LoginService by lazy { serviceFactory.create(LoginService::class.java) }
    val activityService: ActivityService by lazy { serviceFactory.create(ActivityService::class.java) }
}
