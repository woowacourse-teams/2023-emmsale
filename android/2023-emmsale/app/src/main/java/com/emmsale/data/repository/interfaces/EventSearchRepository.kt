package com.emmsale.data.repository.interfaces

import com.emmsale.data.model.EventSearch

interface EventSearchRepository {
    suspend fun getAll(): List<EventSearch>
    suspend fun save(eventSearch: EventSearch)
    suspend fun delete(eventSearch: EventSearch)
    suspend fun deleteAll()
}
