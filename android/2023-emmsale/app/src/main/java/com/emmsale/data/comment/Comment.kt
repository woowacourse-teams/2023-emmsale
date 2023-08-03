package com.emmsale.data.comment

import com.emmsale.data.member.Member1
import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val author: Member1,
    val content: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val deleted: Boolean,
    val childComments: List<Comment>,
)
