package com.emmsale.di.modules.service

import com.emmsale.data.common.ServiceFactory
import com.emmsale.data.messageRoom.MessageRoomService
import com.emmsale.data.service.ActivityService
import com.emmsale.data.service.BlockedMemberService
import com.emmsale.data.service.CommentService
import com.emmsale.data.service.EventService
import com.emmsale.data.service.EventTagService
import com.emmsale.data.service.FcmTokenService
import com.emmsale.data.service.FeedService
import com.emmsale.data.service.LoginService
import com.emmsale.data.service.MemberService
import com.emmsale.data.service.MyPostService
import com.emmsale.data.service.NotificationService
import com.emmsale.data.service.PostService
import com.emmsale.data.service.RecruitmentService
import com.emmsale.data.service.ScrappedEventService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {
    @Provides
    fun provideLoginService(
        serviceFactory: ServiceFactory,
    ): LoginService = serviceFactory.create(LoginService::class.java)

    @Provides
    @Singleton
    fun provideActivityService(
        serviceFactory: ServiceFactory,
    ): ActivityService = serviceFactory.create(ActivityService::class.java)

    @Provides
    @Singleton
    fun provideMemberService(
        serviceFactory: ServiceFactory,
    ): MemberService = serviceFactory.create(MemberService::class.java)

    @Provides
    @Singleton
    fun provideEventService(
        serviceFactory: ServiceFactory,
    ): EventService = serviceFactory.create(EventService::class.java)

    @Provides
    @Singleton
    fun provideFcmTokenService(
        serviceFactory: ServiceFactory,
    ): FcmTokenService = serviceFactory.create(FcmTokenService::class.java)

    @Provides
    @Singleton
    fun provideFeedService(
        serviceFactory: ServiceFactory,
    ): FeedService = serviceFactory.create(FeedService::class.java)

    @Provides
    @Singleton
    fun provideCommentService(
        serviceFactory: ServiceFactory,
    ): CommentService = serviceFactory.create(CommentService::class.java)

    @Provides
    @Singleton
    fun provideEventTagService(
        serviceFactory: ServiceFactory,
    ): EventTagService = serviceFactory.create(EventTagService::class.java)

    @Provides
    @Singleton
    fun provideRecruitmentService(
        serviceFactory: ServiceFactory,
    ): RecruitmentService = serviceFactory.create(RecruitmentService::class.java)

    @Provides
    @Singleton
    fun provideNotificationService(
        serviceFactory: ServiceFactory,
    ): NotificationService = serviceFactory.create(NotificationService::class.java)

    @Provides
    @Singleton
    fun provideScrappedEventService(
        serviceFactory: ServiceFactory,
    ): ScrappedEventService = serviceFactory.create(ScrappedEventService::class.java)

    @Provides
    @Singleton
    fun provideBlockedMemberService(
        serviceFactory: ServiceFactory,
    ): BlockedMemberService = serviceFactory.create(BlockedMemberService::class.java)

    @Provides
    @Singleton
    fun provideMyPostService(
        serviceFactory: ServiceFactory,
    ): MyPostService = serviceFactory.create(MyPostService::class.java)

    @Provides
    @Singleton
    fun providePostService(
        serviceFactory: ServiceFactory,
    ): PostService = serviceFactory.create(PostService::class.java)

    @Provides
    @Singleton
    fun provideMessageRoomService(
        serviceFactory: ServiceFactory,
    ): MessageRoomService = serviceFactory.create(MessageRoomService::class.java)
}
