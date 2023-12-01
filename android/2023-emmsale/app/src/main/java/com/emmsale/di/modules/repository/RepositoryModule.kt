package com.emmsale.di.modules.repository

import com.emmsale.data.repository.concretes.DefaultActivityRepository
import com.emmsale.data.repository.concretes.DefaultBlockedMemberRepository
import com.emmsale.data.repository.concretes.DefaultCommentRepository
import com.emmsale.data.repository.concretes.DefaultCompetitionStatusRepository
import com.emmsale.data.repository.concretes.DefaultConferenceStatusRepository
import com.emmsale.data.repository.concretes.DefaultConfigRepository
import com.emmsale.data.repository.concretes.DefaultEventRepository
import com.emmsale.data.repository.concretes.DefaultEventSearchRepository
import com.emmsale.data.repository.concretes.DefaultEventTagRepository
import com.emmsale.data.repository.concretes.DefaultFcmTokenRepository
import com.emmsale.data.repository.concretes.DefaultFeedRepository
import com.emmsale.data.repository.concretes.DefaultLoginRepository
import com.emmsale.data.repository.concretes.DefaultMemberRepository
import com.emmsale.data.repository.concretes.DefaultMessageRoomRepository
import com.emmsale.data.repository.concretes.DefaultNotificationRepository
import com.emmsale.data.repository.concretes.DefaultRecruitmentRepository
import com.emmsale.data.repository.concretes.DefaultTokenRepository
import com.emmsale.data.repository.interfaces.ActivityRepository
import com.emmsale.data.repository.interfaces.BlockedMemberRepository
import com.emmsale.data.repository.interfaces.CommentRepository
import com.emmsale.data.repository.interfaces.CompetitionStatusRepository
import com.emmsale.data.repository.interfaces.ConferenceStatusRepository
import com.emmsale.data.repository.interfaces.ConfigRepository
import com.emmsale.data.repository.interfaces.EventRepository
import com.emmsale.data.repository.interfaces.EventSearchRepository
import com.emmsale.data.repository.interfaces.EventTagRepository
import com.emmsale.data.repository.interfaces.FcmTokenRepository
import com.emmsale.data.repository.interfaces.FeedRepository
import com.emmsale.data.repository.interfaces.LoginRepository
import com.emmsale.data.repository.interfaces.MemberRepository
import com.emmsale.data.repository.interfaces.MessageRoomRepository
import com.emmsale.data.repository.interfaces.NotificationRepository
import com.emmsale.data.repository.interfaces.RecruitmentRepository
import com.emmsale.data.repository.interfaces.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindLoginRepository(
        impl: DefaultLoginRepository,
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        impl: DefaultTokenRepository,
    ): TokenRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(
        impl: DefaultActivityRepository,
    ): ActivityRepository

    @Binds
    @Singleton
    abstract fun bindMemberRepository(
        impl: DefaultMemberRepository,
    ): MemberRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        impl: DefaultEventRepository,
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindEventSearchRepository(
        impl: DefaultEventSearchRepository,
    ): EventSearchRepository

    @Binds
    @Singleton
    abstract fun bindFcmTokenRepository(
        impl: DefaultFcmTokenRepository,
    ): FcmTokenRepository

    @Binds
    @Singleton
    abstract fun bindFeedRepository(
        impl: DefaultFeedRepository,
    ): FeedRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        impl: DefaultCommentRepository,
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindConferenceStatusRepository(
        impl: DefaultConferenceStatusRepository,
    ): ConferenceStatusRepository

    @Binds
    @Singleton
    abstract fun bindCompetitionStatusRepository(
        impl: DefaultCompetitionStatusRepository,
    ): CompetitionStatusRepository

    @Binds
    @Singleton
    abstract fun bindEventTagRepository(
        impl: DefaultEventTagRepository,
    ): EventTagRepository

    @Binds
    @Singleton
    abstract fun bindRecruitmentRepository(
        impl: DefaultRecruitmentRepository,
    ): RecruitmentRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: DefaultNotificationRepository,
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindBlockedMemberRepository(
        impl: DefaultBlockedMemberRepository,
    ): BlockedMemberRepository

    @Binds
    @Singleton
    abstract fun bindConfigRepository(
        impl: DefaultConfigRepository,
    ): ConfigRepository

    @Binds
    @Singleton
    abstract fun bindMessageRoomRepository(
        impl: DefaultMessageRoomRepository,
    ): MessageRoomRepository
}
