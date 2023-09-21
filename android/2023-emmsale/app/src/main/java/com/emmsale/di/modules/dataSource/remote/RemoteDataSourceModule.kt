package com.emmsale.di.modules.dataSource.remote

import com.emmsale.data.dataSource.remote.EventTagRemoteDataSource
import com.emmsale.data.service.EventTagService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteDataSourceModule {
    @Singleton
    @Provides
    fun provideEventTagRemoteDataSource(
        eventTagService: EventTagService,
    ): EventTagRemoteDataSource = EventTagRemoteDataSource(eventTagService)
}
