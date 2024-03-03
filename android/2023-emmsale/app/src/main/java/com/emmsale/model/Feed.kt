package com.emmsale.model

import java.time.LocalDateTime

data class Feed(
    val id: Long = -1,
    val event: Event = Event(),
    val title: String = "",
    val content: String = "",
    val writer: Member = Member(),
    val imageUrls: List<String> = emptyList(),
    val commentCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.MAX,
    val updatedAt: LocalDateTime = LocalDateTime.MAX,
) {
    val titleImageUrl = imageUrls.firstOrNull()

    val isUpdated = createdAt != updatedAt
}
