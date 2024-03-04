package com.emmsale.model

data class EventSearchHistory(
    val id: Long,
    val query: String,
    val createdAt: Long = System.currentTimeMillis(),
)
