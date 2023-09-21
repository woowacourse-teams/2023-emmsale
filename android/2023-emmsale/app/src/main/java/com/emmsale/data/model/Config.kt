package com.emmsale.data.model

data class Config(
    val isNotificationReceive: Boolean,
    val isFollowNotificationReceive: Boolean,
    val isCommentNotificationReceive: Boolean,
    val isInterestEventNotificationReceive: Boolean,
    val isMessageNotificationReceive: Boolean,
    val isAutoLogin: Boolean,
)
