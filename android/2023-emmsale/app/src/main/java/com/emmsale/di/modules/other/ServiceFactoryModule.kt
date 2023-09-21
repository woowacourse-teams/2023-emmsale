package com.emmsale.di.modules.other

import android.content.Context
import com.emmsale.data.common.ServiceFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceFactoryModule {
    @Singleton
    @Provides
    fun provideServiceFactory(
        @ApplicationContext context: Context,
    ): ServiceFactory = ServiceFactory(context)
}
