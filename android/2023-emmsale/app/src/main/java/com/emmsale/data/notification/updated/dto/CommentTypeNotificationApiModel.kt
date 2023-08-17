package com.emmsale.data.notification.updated.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentTypeNotificationApiModel(
    @SerialName("content")
    val childCommentContent: String,
    @SerialName("eventName")
    val eventName: String,
    @SerialName("commenterImageUrl")
    val commentProfileImageUrl: String,
    @SerialName("parentId")
    val parentId: Long = -1L,
    @SerialName("eventId")
    val eventId: Long = -1L,
)
