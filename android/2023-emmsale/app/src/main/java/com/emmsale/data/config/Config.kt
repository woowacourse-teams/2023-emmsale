package com.emmsale.data.config

data class Config(
    val isNotificationReceive: Boolean,
    val isFollowNotificationReceive: Boolean,
    val isCommentNotificationReceive: Boolean,
    val isInterestEventNotificationReceive: Boolean,
    val isAutoLogin: Boolean,
)
