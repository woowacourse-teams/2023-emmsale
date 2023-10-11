package com.emmsale.data.model

data class EventSearch(
    val id: Long,
    val query: String,
    val createdAt: Long = System.currentTimeMillis(),
)
