package com.emmsale.data.persistence.database

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideKerdyDatabase(
        @ApplicationContext context: Context,
    ): KerdyDatabase = KerdyDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideEventSearchDao(
        database: KerdyDatabase,
    ): EventSearchHistoryDao = database.eventDao()
}
