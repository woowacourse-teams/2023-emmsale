package com.emmsale.data.network.di

import com.emmsale.data.network.service.ActivityService
import com.emmsale.data.network.service.BlockedMemberService
import com.emmsale.data.network.service.CommentService
import com.emmsale.data.network.service.EventService
import com.emmsale.data.network.service.EventTagService
import com.emmsale.data.network.service.FcmTokenService
import com.emmsale.data.network.service.FeedService
import com.emmsale.data.network.service.LoginService
import com.emmsale.data.network.service.MemberService
import com.emmsale.data.network.service.MessageRoomService
import com.emmsale.data.network.service.NotificationService
import com.emmsale.data.network.service.RecruitmentService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideLoginService(
        retrofit: Retrofit
    ): LoginService = retrofit.create(LoginService::class.java)

    @Provides
    @Singleton
    fun provideActivityService(
        retrofit: Retrofit
    ): ActivityService =
        retrofit.create(ActivityService::class.java)

    @Provides
    @Singleton
    fun provideMemberService(
        retrofit: Retrofit
    ): MemberService = retrofit.create(MemberService::class.java)

    @Provides
    @Singleton
    fun provideEventService(
        retrofit: Retrofit
    ): EventService = retrofit.create(EventService::class.java)

    @Provides
    @Singleton
    fun provideFcmTokenService(
        retrofit: Retrofit
    ): FcmTokenService = retrofit.create(FcmTokenService::class.java)

    @Provides
    @Singleton
    fun provideFeedService(
        retrofit: Retrofit
    ): FeedService = retrofit.create(FeedService::class.java)

    @Provides
    @Singleton
    fun provideCommentService(
        retrofit: Retrofit
    ): CommentService = retrofit.create(CommentService::class.java)

    @Provides
    @Singleton
    fun provideEventTagService(
        retrofit: Retrofit
    ): EventTagService = retrofit.create(EventTagService::class.java)

    @Provides
    @Singleton
    fun provideRecruitmentService(
        retrofit: Retrofit
    ): RecruitmentService = retrofit.create(RecruitmentService::class.java)

    @Provides
    @Singleton
    fun provideNotificationService(
        retrofit: Retrofit
    ): NotificationService = retrofit.create(NotificationService::class.java)

    @Provides
    @Singleton
    fun provideBlockedMemberService(
        retrofit: Retrofit
    ): BlockedMemberService = retrofit.create(BlockedMemberService::class.java)

    @Provides
    @Singleton
    fun provideMessageRoomService(
        retrofit: Retrofit
    ): MessageRoomService = retrofit.create(MessageRoomService::class.java)
}
