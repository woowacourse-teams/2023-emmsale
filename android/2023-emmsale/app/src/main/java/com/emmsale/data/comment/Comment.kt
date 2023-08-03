package com.emmsale.data.comment

import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val authorId: Long,
    val authorName: String,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deleted: Boolean,
    val childComments: List<Comment>,
)
