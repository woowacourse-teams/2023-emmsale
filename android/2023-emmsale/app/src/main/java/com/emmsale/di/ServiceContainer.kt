package com.emmsale.di

import com.emmsale.data.activity.ActivityService
import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.conference.ConferenceService
import com.emmsale.data.fcmToken.FcmTokenService
import com.emmsale.data.login.LoginService
import com.emmsale.data.member.MemberService

class ServiceContainer(serviceFactory: ServiceFactory) {
    val loginService: LoginService by lazy { serviceFactory.create(LoginService::class.java) }
    val activityService: ActivityService by lazy { serviceFactory.create(ActivityService::class.java) }
    val memberService: MemberService by lazy { serviceFactory.create(MemberService::class.java) }
    val conferenceService: ConferenceService by lazy { serviceFactory.create(ConferenceService::class.java) }
    val fcmTokenService: FcmTokenService by lazy { serviceFactory.create(FcmTokenService::class.java) }
}
