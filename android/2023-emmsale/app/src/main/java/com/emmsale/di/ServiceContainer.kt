package com.emmsale.di

import com.emmsale.data.activity.ActivityService
import com.emmsale.data.blockedMember.BlockedMemberService
import com.emmsale.data.comment.CommentService
import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.event.EventService
import com.emmsale.data.eventTag.EventTagService
import com.emmsale.data.eventTag.FakeEventTagService
import com.emmsale.data.eventdetail.EventDetailService
import com.emmsale.data.fcmToken.FcmTokenService
import com.emmsale.data.login.LoginService
import com.emmsale.data.member.MemberService
import com.emmsale.data.notification.NotificationService
import com.emmsale.data.recruitment.RecruitmentService

class ServiceContainer(serviceFactory: ServiceFactory) {
    val loginService: LoginService by lazy { serviceFactory.create(LoginService::class.java) }
    val activityService: ActivityService by lazy { serviceFactory.create(ActivityService::class.java) }
    val memberService: MemberService by lazy { serviceFactory.create(MemberService::class.java) }
    val eventService: EventService by lazy { serviceFactory.create(EventService::class.java) }
    val fcmTokenService: FcmTokenService by lazy { serviceFactory.create(FcmTokenService::class.java) }
    val commentService: CommentService by lazy { serviceFactory.create(CommentService::class.java) }
    val eventTagService: EventTagService by lazy { FakeEventTagService() }
    val eventDetailService: EventDetailService by lazy { serviceFactory.create(EventDetailService::class.java) }
    val recruitmentService: RecruitmentService by lazy { serviceFactory.create(RecruitmentService::class.java) }
    val notificationService: NotificationService by lazy { serviceFactory.create(NotificationService::class.java) }
    val blockedMemberService: BlockedMemberService by lazy {
        serviceFactory.create(BlockedMemberService::class.java)
    }
}
