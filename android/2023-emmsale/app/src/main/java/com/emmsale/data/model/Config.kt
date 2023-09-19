package com.emmsale.data.model

data class Config(
    val isNotificationReceive: Boolean,
    val isFollowNotificationReceive: Boolean,
    val isCommentNotificationReceive: Boolean,
    val isInterestEventNotificationReceive: Boolean,
    val isAutoLogin: Boolean,
)
