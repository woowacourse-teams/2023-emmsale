package com.emmsale.di

import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.activity.ActivityRepositoryImpl
import com.emmsale.data.comment.CommentRepository
import com.emmsale.data.comment.CommentRepositoryImpl
import com.emmsale.data.conference.ConferenceRepository
import com.emmsale.data.conference.ConferenceRepositoryImpl
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
import com.emmsale.data.notification.NotificationRepository
import com.emmsale.data.notification.NotificationRepositoryImpl
import com.emmsale.data.recruitment.RecruitmentRepository
import com.emmsale.data.recruitment.RecruitmentRepositoryImpl
import com.emmsale.data.token.TokenRepository
import com.emmsale.data.token.TokenRepositoryImpl
import com.emmsale.data.uid.UidRepository
import com.emmsale.data.uid.UidRepositoryImpl

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
    val conferenceRepository: ConferenceRepository by lazy {
        ConferenceRepositoryImpl(conferenceService = serviceContainer.conferenceService)
    }
    val fcmTokenRepository: FcmTokenRepository by lazy {
        FcmTokenRepositoryImpl(fcmTokenService = serviceContainer.fcmTokenService)
    }
    val commentRepository: CommentRepository by lazy {
        CommentRepositoryImpl(
            commentService = serviceContainer.commentService,
        )
    }
    val eventTagRepository: EventTagRepository by lazy {
        EventTagRepositoryImpl(eventTagService = serviceContainer.eventTagService)
    }
    val eventDetailRepository: EventDetailRepository by lazy {
        EventDetailRepositoryImpl(eventDetailService = serviceContainer.eventDetailService)
    }
    val uidRepository: UidRepository by lazy {
        UidRepositoryImpl(preferenceContainer.preference)
    }
    val recruitmentRepository: RecruitmentRepository by lazy {
        RecruitmentRepositoryImpl(
            uidRepository = uidRepository,
            recruitmentService = serviceContainer.recruitmentService,
        )
    }
    val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl(notificationService = serviceContainer.notificationService)
    }
}
