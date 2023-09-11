package com.emmsale.data.config

interface ConfigRepository {
    fun getConfig(): Config
    fun saveNotificationReceiveConfig(isReceive: Boolean)
    fun saveAutoLoginConfig(isAutoLogin: Boolean)
    fun saveFollowNotificationReceiveConfig(isReceive: Boolean)
    fun saveChildNotificationReceiveConfig(isReceive: Boolean)
    fun saveInterestEventNotificationReceiveConfig(isReceive: Boolean)
}
