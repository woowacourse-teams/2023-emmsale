package com.emmsale.data.model

data class ScrappedEvent(
    val scrapId: Long,
    val eventId: Long,
    val name: String,
    val status: ConferenceStatus,
    val imageUrl: String?,
    val tags: List<String> = listOf(),
)
