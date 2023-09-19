package com.emmsale.data.model

import java.time.LocalDateTime

data class FeedDetail(
    val id: Long,
    val eventId: Long,
    val title: String,
    val content: String,
    val writer: Writer,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
