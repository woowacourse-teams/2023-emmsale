package com.emmsale.data.model

import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val eventId: Long,
    val eventName: String,
    val authorId: Long,
    val authorName: String,
    val authorImageUrl: String,
    val parentId: Long?,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deleted: Boolean,
    val childComments: List<Comment>,
)
