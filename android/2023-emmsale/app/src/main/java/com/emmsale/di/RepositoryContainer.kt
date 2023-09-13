package com.emmsale.di

import com.emmsale.data.repository.ActivityRepository
import com.emmsale.data.repository.BlockedMemberRepository
import com.emmsale.data.repository.CommentRepository
import com.emmsale.data.repository.CompetitionStatusRepository
import com.emmsale.data.repository.ConferenceStatusRepository
import com.emmsale.data.repository.ConfigRepository
import com.emmsale.data.repository.DefaultActivityRepository
import com.emmsale.data.repository.DefaultBlockedMemberRepository
import com.emmsale.data.repository.DefaultCommentRepository
import com.emmsale.data.repository.DefaultCompetitionStatusRepository
import com.emmsale.data.repository.DefaultConferenceStatusRepository
import com.emmsale.data.repository.DefaultConfigRepository
import com.emmsale.data.repository.DefaultEventDetailRepository
import com.emmsale.data.repository.DefaultEventRepository
import com.emmsale.data.repository.DefaultEventTagRepository
import com.emmsale.data.repository.DefaultFcmTokenRepository
import com.emmsale.data.repository.DefaultLoginRepository
import com.emmsale.data.repository.DefaultMemberRepository
import com.emmsale.data.repository.DefaultMyPostRepository
import com.emmsale.data.repository.DefaultNotificationRepository
import com.emmsale.data.repository.DefaultRecruitmentRepository
import com.emmsale.data.repository.DefaultScrappedEventRepository
import com.emmsale.data.repository.DefaultTokenRepository
import com.emmsale.data.repository.EventDetailRepository
import com.emmsale.data.repository.EventRepository
import com.emmsale.data.repository.EventTagRepository
import com.emmsale.data.repository.FcmTokenRepository
import com.emmsale.data.repository.LoginRepository
import com.emmsale.data.repository.MemberRepository
import com.emmsale.data.repository.MyPostRepository
import com.emmsale.data.repository.NotificationRepository
import com.emmsale.data.repository.RecruitmentRepository
import com.emmsale.data.repository.ScrappedEventRepository
import com.emmsale.data.repository.TokenRepository

class RepositoryContainer(
    serviceContainer: ServiceContainer,
    preferenceContainer: SharedPreferenceContainer,
    remoteDataSourceContainer: RemoteDataSourceContainer = RemoteDataSourceContainer(
        serviceContainer,
    ),
) {
    val loginRepository: LoginRepository by lazy {
        DefaultLoginRepository(loginService = serviceContainer.loginService)
    }
    val tokenRepository: TokenRepository by lazy {
        DefaultTokenRepository(preference = preferenceContainer.preference)
    }
    val activityRepository: ActivityRepository by lazy {
        DefaultActivityRepository(activityService = serviceContainer.activityService)
    }
    val memberRepository: MemberRepository by lazy {
        DefaultMemberRepository(memberService = serviceContainer.memberService)
    }
    val eventRepository: EventRepository by lazy {
        DefaultEventRepository(eventService = serviceContainer.conferenceService)
    }
    val fcmTokenRepository: FcmTokenRepository by lazy {
        DefaultFcmTokenRepository(fcmTokenService = serviceContainer.fcmTokenService)
    }
    val commentRepository: CommentRepository by lazy {
        DefaultCommentRepository(
            commentService = serviceContainer.commentService,
        )
    }
    val conferenceStatusRepository: ConferenceStatusRepository by lazy { DefaultConferenceStatusRepository() }
    val competitionStatusRepository: CompetitionStatusRepository by lazy { DefaultCompetitionStatusRepository() }
    val eventTagRepository: EventTagRepository by lazy {
        DefaultEventTagRepository(remoteDataSourceContainer.eventTagRemoteDataSource)
    }
    val eventDetailRepository: EventDetailRepository by lazy {
        DefaultEventDetailRepository(
            eventDetailService = serviceContainer.eventDetailService,
        )
    }
    val recruitmentRepository: RecruitmentRepository by lazy {
        DefaultRecruitmentRepository(
            tokenRepository = tokenRepository,
            recruitmentService = serviceContainer.recruitmentService,
        )
    }
    val notificationRepository: NotificationRepository by lazy {
        DefaultNotificationRepository(notificationService = serviceContainer.notificationService)
    }
    val scrappedEventRepository: ScrappedEventRepository by lazy {
        DefaultScrappedEventRepository(scrappedEventService = serviceContainer.scrappedEventService)
    }
    val blockedMemberRepository: BlockedMemberRepository by lazy {
        DefaultBlockedMemberRepository(blockedMemberService = serviceContainer.blockedMemberService)
    }
    val configRepository: ConfigRepository by lazy {
        DefaultConfigRepository(preference = preferenceContainer.preference)
    }
    val myPostRepository: MyPostRepository by lazy {
        DefaultMyPostRepository(
            myPostService = serviceContainer.myPostService,
            tokenRepository = tokenRepository,
        )
    }
}
