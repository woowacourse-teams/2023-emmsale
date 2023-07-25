package com.emmsale.presentation.di

import com.emmsale.data.career.CareerService
import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.login.LoginService

class ServiceContainer(serviceFactory: ServiceFactory) {
    val loginService: LoginService by lazy { serviceFactory.create(LoginService::class.java) }
    val careerService: CareerService by lazy { serviceFactory.create(CareerService::class.java) }
}
