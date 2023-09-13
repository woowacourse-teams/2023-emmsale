package com.emmsale.di

import com.emmsale.data.activity.ActivityService
import com.emmsale.data.blockedMember.BlockedMemberService
import com.emmsale.data.comment.CommentService
import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.event.EventService
import com.emmsale.data.eventTag.remote.EventTagService
import com.emmsale.data.eventdetail.EventDetailService
import com.emmsale.data.fcmToken.FcmTokenService
import com.emmsale.data.login.LoginService
import com.emmsale.data.member.MemberService
import com.emmsale.data.messageRoom.MessageRoomService
import com.emmsale.data.myPost.MyPostService
import com.emmsale.data.notification.NotificationService
import com.emmsale.data.recruitment.RecruitmentService
import com.emmsale.data.scrap.ScrappedEventService

class ServiceContainer(serviceFactory: ServiceFactory) {
    val loginService: LoginService by lazy { serviceFactory.create(LoginService::class.java) }
    val activityService: ActivityService by lazy { serviceFactory.create(ActivityService::class.java) }
    val memberService: MemberService by lazy { serviceFactory.create(MemberService::class.java) }
    val conferenceService: EventService by lazy { serviceFactory.create(EventService::class.java) }
    val fcmTokenService: FcmTokenService by lazy { serviceFactory.create(FcmTokenService::class.java) }
    val commentService: CommentService by lazy { serviceFactory.create(CommentService::class.java) }
    val eventTagService: EventTagService by lazy { serviceFactory.create(EventTagService::class.java) }
    val eventDetailService: EventDetailService by lazy { serviceFactory.create(EventDetailService::class.java) }
    val recruitmentService: RecruitmentService by lazy { serviceFactory.create(RecruitmentService::class.java) }
    val notificationService: NotificationService by lazy { serviceFactory.create(NotificationService::class.java) }
    val scrappedEventService: ScrappedEventService by lazy {
        serviceFactory.create(
            ScrappedEventService::class.java,
        )
    }
    val blockedMemberService: BlockedMemberService by lazy {
        serviceFactory.create(
            BlockedMemberService::class.java,
        )
    }
    val myPostService: MyPostService by lazy { serviceFactory.create(MyPostService::class.java) }
    val messageRoomService: MessageRoomService by lazy { serviceFactory.create(MessageRoomService::class.java) }
}
