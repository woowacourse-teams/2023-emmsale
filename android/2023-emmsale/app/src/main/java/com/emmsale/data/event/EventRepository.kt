package com.emmsale.data.event

interface EventRepository {
    suspend fun getEvents(
        year: Int,
        month: Int,
        status: String,
        tags: List<String>,
    )
}
