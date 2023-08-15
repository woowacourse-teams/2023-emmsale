package com.emmsale.di

import com.emmsale.data.eventTag.local.EventTagLocalDataSource

class LocalDataSourceContainer {
    val eventTagLocalDataSource: EventTagLocalDataSource by lazy { EventTagLocalDataSource() }
}