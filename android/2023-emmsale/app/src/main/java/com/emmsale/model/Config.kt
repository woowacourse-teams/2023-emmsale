package com.emmsale.model

data class Config(
    val isNotificationReceive: Boolean,
    val isCommentNotificationReceive: Boolean,
    val isInterestEventNotificationReceive: Boolean,
    val isMessageNotificationReceive: Boolean,
    val isAutoLogin: Boolean,
)
