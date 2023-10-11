package com.emmsale.di.modules.dao

import com.emmsale.data.common.database.KerdyDatabase
import com.emmsale.data.common.database.dao.EventSearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DaoModule {
    @Provides
    @Singleton
    fun provideEventSearchDao(
        database: KerdyDatabase,
    ): EventSearchDao = database.eventDao()
}
