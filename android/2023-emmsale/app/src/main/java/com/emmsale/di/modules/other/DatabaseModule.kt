package com.emmsale.di.modules.other

import android.content.Context
import com.emmsale.data.common.database.KerdyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideKerdyDatabase(
        @ApplicationContext context: Context,
    ): KerdyDatabase = KerdyDatabase.getInstance(context)
}
