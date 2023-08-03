package com.emmsale.di

import com.emmsale.data.activity.ActivityService
import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.conference.ConferenceService
import com.emmsale.data.eventTag.EventTagService
import com.emmsale.data.eventTag.FakeEventTagService
import com.emmsale.data.eventdetail.EventDetailService
import com.emmsale.data.fcmToken.FcmTokenService
import com.emmsale.data.login.LoginService
import com.emmsale.data.member.MemberService
import com.emmsale.data.notification.NotificationService
import com.emmsale.data.participant.ParticipantService

class ServiceContainer(serviceFactory: ServiceFactory) {
    val loginService: LoginService by lazy { serviceFactory.create(LoginService::class.java) }
    val activityService: ActivityService by lazy { serviceFactory.create(ActivityService::class.java) }
    val memberService: MemberService by lazy { serviceFactory.create(MemberService::class.java) }
    val conferenceService: ConferenceService by lazy { serviceFactory.create(ConferenceService::class.java) }
    val fcmTokenService: FcmTokenService by lazy { serviceFactory.create(FcmTokenService::class.java) }
    val eventTagService: EventTagService by lazy { FakeEventTagService() }
    val eventDetailService: EventDetailService by lazy { serviceFactory.create(EventDetailService::class.java) }
    val participantService: ParticipantService by lazy { serviceFactory.create(ParticipantService::class.java) }
    val notificationService: NotificationService by lazy { serviceFactory.create(NotificationService::class.java) }
}
