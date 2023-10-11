package com.emmsale.data.model

data class EventSearch(
    val id: Long = -1L,
    val query: String,
    val createdAt: Long = System.currentTimeMillis(),
)
