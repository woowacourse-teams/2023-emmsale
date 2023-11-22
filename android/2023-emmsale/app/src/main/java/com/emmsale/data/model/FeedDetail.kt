package com.emmsale.data.model

import java.time.LocalDateTime

data class FeedDetail(
    val id: Long,
    val eventId: Long,
    val title: String,
    val content: String,
    val writer: Writer,
    val imageUrls: List<String>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class Feed(
    val id: Long,
    val eventId: Long,
    val title: String,
    val content: String,
    val writer: Member,
    val imageUrls: List<String>,
    val commentCount: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    val titleImageUrl = imageUrls.firstOrNull()
}
