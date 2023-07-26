package com.emmsale.di

import com.emmsale.data.activity.ActivityService
import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.event.EventService
import com.emmsale.data.login.LoginService
import com.emmsale.data.member.MemberService

class ServiceContainer(serviceFactory: ServiceFactory) {
    val loginService: LoginService by lazy { serviceFactory.create(LoginService::class.java) }
    val activityService: ActivityService by lazy { serviceFactory.create(ActivityService::class.java) }
    val memberService: MemberService by lazy { serviceFactory.create(MemberService::class.java) }
    val eventService: EventService by lazy { serviceFactory.create(EventService::class.java) }
}
