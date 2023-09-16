package com.emmsale.data.model

import java.time.LocalDateTime

data class Post(
    val eventId: Long,
    val title: String,
    val content: String,
    val titleImageUrl: String,
    val likeCount: Int,
    val createdAt: LocalDateTime,
    val commentCount: Int,
)
