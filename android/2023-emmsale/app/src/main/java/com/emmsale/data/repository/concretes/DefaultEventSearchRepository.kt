package com.emmsale.data.repository.concretes

import com.emmsale.data.mapper.toData
import com.emmsale.data.mapper.toEntity
import com.emmsale.data.network.di.IoDispatcher
import com.emmsale.data.persistence.database.EventSearchHistoryDao
import com.emmsale.data.persistence.database.EventSearchHistoryEntity
import com.emmsale.data.repository.interfaces.EventSearchRepository
import com.emmsale.model.EventSearchHistory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultEventSearchRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val dao: EventSearchHistoryDao,
) : EventSearchRepository {
    override suspend fun getAll(): List<EventSearchHistory> = withContext(dispatcher) {
        dao.getAll()
            .distinctBy { eventSearch -> eventSearch.query }
            .map(EventSearchHistoryEntity::toData)
    }

    override suspend fun save(searchQuery: String) {
        withContext(dispatcher) {
            dao.deleteByQuery(searchQuery)
            dao.save(EventSearchHistoryEntity(query = searchQuery))
        }
    }

    override suspend fun delete(eventSearch: EventSearchHistory) {
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
