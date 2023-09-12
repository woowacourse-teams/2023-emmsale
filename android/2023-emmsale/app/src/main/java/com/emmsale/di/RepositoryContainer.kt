package com.emmsale.di

import com.emmsale.data.activity.ActivityRepository
import com.emmsale.data.activity.ActivityRepositoryImpl
import com.emmsale.data.blockedMember.BlockedMemberRepository
import com.emmsale.data.blockedMember.BlockedMemberRepositoryImpl
import com.emmsale.data.comment.CommentRepository
import com.emmsale.data.comment.CommentRepositoryImpl
import com.emmsale.data.competitionStatus.CompetitionStatusRepository
import com.emmsale.data.competitionStatus.CompetitionStatusRepositoryImpl
import com.emmsale.data.conferenceStatus.ConferenceStatusRepository
import com.emmsale.data.conferenceStatus.ConferenceStatusRepositoryImpl
import com.emmsale.data.config.ConfigRepository
import com.emmsale.data.config.ConfigRepositoryImpl
import com.emmsale.data.event.EventRepository
import com.emmsale.data.event.EventRepositoryImpl
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
import com.emmsale.data.scrap.ScrappedEventRepository
import com.emmsale.data.scrap.ScrappedEventRepositoryImpl
import com.emmsale.data.token.TokenRepository
import com.emmsale.data.token.TokenRepositoryImpl

class RepositoryContainer(
    serviceContainer: ServiceContainer,
    preferenceContainer: SharedPreferenceContainer,
    remoteDataSourceContainer: RemoteDataSourceContainer = RemoteDataSourceContainer(
        serviceContainer,
    ),
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
        EventRepositoryImpl(eventService = serviceContainer.conferenceService)
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
    val competitionStatusRepository: CompetitionStatusRepository by lazy { CompetitionStatusRepositoryImpl() }
    val eventTagRepository: EventTagRepository by lazy {
        EventTagRepositoryImpl(remoteDataSourceContainer.eventTagRemoteDataSource)
    }
    val eventDetailRepository: EventDetailRepository by lazy {
        EventDetailRepositoryImpl(
            eventDetailService = serviceContainer.eventDetailService,
        )
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
    val scrappedEventRepository: ScrappedEventRepository by lazy {
        ScrappedEventRepositoryImpl(scrappedEventService = serviceContainer.scrappedEventService)
    }
    val blockedMemberRepository: BlockedMemberRepository by lazy {
        BlockedMemberRepositoryImpl(blockedMemberService = serviceContainer.blockedMemberService)
    }
    val configRepository: ConfigRepository by lazy {
        ConfigRepositoryImpl(preference = preferenceContainer.preference)
    }
    val myPostRepository: MyPostRepository by lazy {
        MyPostRepositoryImpl(
            myPostService = serviceContainer.myPostService,
            tokenRepository = tokenRepository,
        )
    }
}
