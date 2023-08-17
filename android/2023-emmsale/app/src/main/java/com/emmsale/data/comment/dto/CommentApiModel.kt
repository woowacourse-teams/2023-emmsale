package com.emmsale.data.comment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentApiModel(
    @SerialName("content")
    val content: String,
    @SerialName("commentId")
    val commentId: Long,
    @SerialName("parentId")
    val parentId: Long?,
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("eventName")
    val eventName: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("memberImageUrl")
    val memberImageUrl: String,
    @SerialName("memberName")
    val memberName: String,
    @SerialName("deleted")
    val deleted: Boolean,
)
