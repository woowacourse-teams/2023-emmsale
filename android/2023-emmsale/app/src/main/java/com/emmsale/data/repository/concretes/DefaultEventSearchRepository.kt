package com.emmsale.data.repository.concretes

import com.emmsale.data.common.database.dao.EventSearchDao
import com.emmsale.data.mapper.toData
import com.emmsale.data.mapper.toEntity
import com.emmsale.data.model.EventSearch
import com.emmsale.data.repository.interfaces.EventSearchRepository
import com.emmsale.di.modules.other.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultEventSearchRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val dao: EventSearchDao,
) : EventSearchRepository {
    override suspend fun getAll(): List<EventSearch> = withContext(dispatcher) {
        dao.getAll().map { it.toData() }
    }

    override suspend fun save(eventSearch: EventSearch) {
        withContext(dispatcher) {
            dao.save(eventSearch.toEntity())
        }
    }

    override suspend fun delete(eventSearch: EventSearch) {
        withContext(dispatcher) {
            dao.delete(eventSearch.toEntity())
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatcher) {
            dao.deleteAll()
        }
    }
}
