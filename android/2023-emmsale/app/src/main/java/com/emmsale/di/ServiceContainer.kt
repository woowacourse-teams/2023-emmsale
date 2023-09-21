package com.emmsale.di

import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.messageRoom.MessageRoomService
import com.emmsale.data.service.ActivityService
import com.emmsale.data.service.BlockedMemberService
import com.emmsale.data.service.CommentService
import com.emmsale.data.service.EventService
import com.emmsale.data.service.EventTagService
import com.emmsale.data.service.FcmTokenService
import com.emmsale.data.service.LoginService
import com.emmsale.data.service.MemberService
import com.emmsale.data.service.MyPostService
import com.emmsale.data.service.NotificationService
import com.emmsale.data.service.RecruitmentService
import com.emmsale.data.service.ScrappedEventService

class ServiceContainer(serviceFactory: ServiceFactory) {
    val loginService: LoginService by lazy { serviceFactory.create(LoginService::class.java) }
    val activityService: ActivityService by lazy { serviceFactory.create(ActivityService::class.java) }
    val memberService: MemberService by lazy { serviceFactory.create(MemberService::class.java) }
    val eventService: EventService by lazy { serviceFactory.create(EventService::class.java) }
    val fcmTokenService: FcmTokenService by lazy { serviceFactory.create(FcmTokenService::class.java) }
    val commentService: CommentService by lazy { serviceFactory.create(CommentService::class.java) }
    val eventTagService: EventTagService by lazy { serviceFactory.create(EventTagService::class.java) }
    val recruitmentService: RecruitmentService by lazy { serviceFactory.create(RecruitmentService::class.java) }
    val notificationService: NotificationService by lazy { serviceFactory.create(NotificationService::class.java) }
    val scrappedEventService: ScrappedEventService by lazy {
        serviceFactory.create(
            ScrappedEventService::class.java,
        )
    }
    val blockedMemberService: BlockedMemberService by lazy {
        serviceFactory.create(BlockedMemberService::class.java)
    }
    val myPostService: MyPostService by lazy {
        serviceFactory.create(MyPostService::class.java)
    }
    val messageRoomService: MessageRoomService by lazy { serviceFactory.create(MessageRoomService::class.java) }
}
