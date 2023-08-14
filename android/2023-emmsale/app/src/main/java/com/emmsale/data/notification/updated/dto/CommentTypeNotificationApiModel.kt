package com.emmsale.data.notification.updated.dto

import kotlinx.serialization.SerialName

data class CommentTypeNotificationApiModel(
    @SerialName("content")
    val childCommentContent: String,
    @SerialName("eventName")
    val eventName: String,
    @SerialName("commenterImageUrl")
    val commentProfileImageUrl: String,
)
