package com.emmsale.data.repository.interfaces

import com.emmsale.data.model.EventSearchHistory

interface EventSearchRepository {
    suspend fun getAll(): List<EventSearchHistory>
    suspend fun save(searchQuery: String)
    suspend fun delete(eventSearch: EventSearchHistory)
    suspend fun deleteAll()
}
