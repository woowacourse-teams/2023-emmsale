package com.emmsale.di

import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.activity.ActivityRepositoryImpl
import com.emmsale.data.comment.CommentRepository
import com.emmsale.data.comment.CommentRepositoryImpl
import com.emmsale.data.conference.EventRepository
import com.emmsale.data.conference.EventRepositoryImpl
import com.emmsale.data.conferenceStatus.ConferenceStatusRepository
import com.emmsale.data.conferenceStatus.ConferenceStatusRepositoryImpl
import com.emmsale.data.eventTag.EventTagRepository
import com.emmsale.data.eventTag.EventTagRepositoryImpl
import com.emmsale.data.eventdetail.EventDetailRepository
import com.emmsale.data.eventdetail.EventDetailRepositoryImpl
import com.emmsale.data.fcmToken.FcmTokenRepository
import com.emmsale.data.fcmToken.FcmTokenRepositoryImpl
import com.emmsale.data.login.LoginRepository
import com.emmsale.data.login.LoginRepositoryImpl
import com.emmsale.data.member.MemberRepository
import com.emmsale.data.member.MemberRepositoryImpl
import com.emmsale.data.myPost.MyPostRepository
import com.emmsale.data.myPost.MyPostRepositoryImpl
import com.emmsale.data.notification.NotificationRepository
import com.emmsale.data.notification.NotificationRepositoryImpl
import com.emmsale.data.recruitment.RecruitmentRepository
import com.emmsale.data.recruitment.RecruitmentRepositoryImpl
import com.emmsale.data.token.TokenRepository
import com.emmsale.data.token.TokenRepositoryImpl

class RepositoryContainer(
    serviceContainer: ServiceContainer,
    preferenceContainer: SharedPreferenceContainer,
) {
    val loginRepository: LoginRepository by lazy {
        LoginRepositoryImpl(loginService = serviceContainer.loginService)
    }
    val tokenRepository: TokenRepository by lazy {
        TokenRepositoryImpl(preference = preferenceContainer.preference)
    }
    val activityRepository: ActivityRepository by lazy {
        ActivityRepositoryImpl(activityService = serviceContainer.activityService)
    }
    val memberRepository: MemberRepository by lazy {
        MemberRepositoryImpl(memberService = serviceContainer.memberService)
    }
    val eventRepository: EventRepository by lazy {
        EventRepositoryImpl(conferenceService = serviceContainer.conferenceService)
    }
    val fcmTokenRepository: FcmTokenRepository by lazy {
        FcmTokenRepositoryImpl(fcmTokenService = serviceContainer.fcmTokenService)
    }
    val commentRepository: CommentRepository by lazy {
        CommentRepositoryImpl(
            commentService = serviceContainer.commentService,
        )
    }
    val conferenceStatusRepository: ConferenceStatusRepository by lazy { ConferenceStatusRepositoryImpl() }
    val eventTagRepository: EventTagRepository by lazy {
        EventTagRepositoryImpl(eventTagService = serviceContainer.eventTagService)
    }
    val eventDetailRepository: EventDetailRepository by lazy {
        EventDetailRepositoryImpl(eventDetailService = serviceContainer.eventDetailService)
    }
    val recruitmentRepository: RecruitmentRepository by lazy {
        RecruitmentRepositoryImpl(
            tokenRepository = tokenRepository,
            recruitmentService = serviceContainer.recruitmentService,
        )
    }
    val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl(notificationService = serviceContainer.notificationService)
    }
    val myPostRepository: MyPostRepository by lazy {
        MyPostRepositoryImpl()
    }
}
