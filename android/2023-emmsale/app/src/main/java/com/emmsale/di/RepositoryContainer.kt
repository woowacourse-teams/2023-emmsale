package com.emmsale.di

import com.emmsale.data.messageRoom.MessageRoomRepository
import com.emmsale.data.messageRoom.MessageRoomRepositoryImpl
import com.emmsale.data.repository.concretes.DefaultActivityRepository
import com.emmsale.data.repository.concretes.DefaultBlockedMemberRepository
import com.emmsale.data.repository.concretes.DefaultCommentRepository
import com.emmsale.data.repository.concretes.DefaultCompetitionStatusRepository
import com.emmsale.data.repository.concretes.DefaultConferenceStatusRepository
import com.emmsale.data.repository.concretes.DefaultConfigRepository
import com.emmsale.data.repository.concretes.DefaultEventRepository
import com.emmsale.data.repository.concretes.DefaultEventTagRepository
import com.emmsale.data.repository.concretes.DefaultFcmTokenRepository
import com.emmsale.data.repository.concretes.DefaultLoginRepository
import com.emmsale.data.repository.concretes.DefaultMemberRepository
import com.emmsale.data.repository.concretes.DefaultMyPostRepository
import com.emmsale.data.repository.concretes.DefaultNotificationRepository
import com.emmsale.data.repository.concretes.DefaultRecruitmentRepository
import com.emmsale.data.repository.concretes.DefaultScrappedEventRepository
import com.emmsale.data.repository.concretes.DefaultTokenRepository
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.CompetitionStatusRepository
import com.emmsale.data.repository.interfaces.ConferenceStatusRepository
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.data.repository.interfaces.FcmTokenRepository
import com.emmsale.data.repository.interfaces.LoginRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.MyPostRepository
import com.emmsale.data.repository.interfaces.NotificationRepository
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.data.repository.interfaces.ScrappedEventRepository
import com.emmsale.data.repository.interfaces.TokenRepository

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
        DefaultEventRepository(eventService = serviceContainer.eventService)
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
    val messageRoomRepository: MessageRoomRepository by lazy {
        MessageRoomRepositoryImpl(
            messageRoomService = serviceContainer.messageRoomService,
        )
    }
}
