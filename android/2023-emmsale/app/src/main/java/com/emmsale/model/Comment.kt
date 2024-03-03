package com.emmsale.model

import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val feed: Feed = Feed(),
    val writer: Member = Member(),
    val parentCommentId: Long? = null,
    val content: String = "",
    val createdAt: LocalDateTime = LocalDateTime.MAX,
    val updatedAt: LocalDateTime = LocalDateTime.MAX,
    val isDeleted: Boolean = false,
    val childComments: List<Comment> = emptyList(),
) {
    val isUpdated: Boolean = createdAt != updatedAt
}
