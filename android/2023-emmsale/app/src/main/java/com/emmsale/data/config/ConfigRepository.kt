package com.emmsale.data.config

interface ConfigRepository {
    fun getConfig(): Config
    fun saveAllNotificationReceiveConfig(isReceive: Boolean)
    fun saveAutoLoginConfig(isAutoLogin: Boolean)
    fun saveFollowNotificationReceiveConfig(isReceive: Boolean)
    fun saveCommentNotificationReceiveConfig(isReceive: Boolean)
    fun saveInterestEventNotificationReceiveConfig(isReceive: Boolean)
}
