package com.emmsale.data.config

interface ConfigRepository {
    suspend fun getNotificationReceiveConfig(): Config
    suspend fun saveNotificationReceiveConfig(isReceive: Boolean)
}
