package com.emmsale.di

import com.emmsale.data.eventTag.remote.EventTagRemoteDataSource

class RemoteDataSourceContainer(
    serviceContainer: ServiceContainer,
) {
    val eventTagRemoteDataSource: EventTagRemoteDataSource by lazy {
        EventTagRemoteDataSource(serviceContainer.eventTagService)
    }
}
