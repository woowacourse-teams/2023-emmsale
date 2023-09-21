package com.emmsale.data.model

import java.time.LocalDateTime

data class Post(
    val id: Long,
    val eventId: Long,
    val title: String,
    val content: String,
    val titleImageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val commentCount: Int,
)
