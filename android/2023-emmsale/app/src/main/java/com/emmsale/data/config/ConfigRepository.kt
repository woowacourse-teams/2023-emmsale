package com.emmsale.data.config

interface ConfigRepository {
    fun getConfig(): Config
    fun saveNotificationReceiveConfig(isReceive: Boolean)
    fun saveAutoLoginConfig(isAutoLogin: Boolean)

    fun getFollowNotificationReceiveConfig(): Boolean
    fun saveFollowNotificationReceiveConfig(isReceive: Boolean)
    fun getChildCommentNotificationReceiveConfig(): Boolean
    fun saveChildNotificationReceiveConfig(isReceive: Boolean)
    fun getInterestEventNotificationReceiveConfig(): Boolean
    fun saveInterestEventNotificationReceiveConfig(isReceive: Boolean)
}
