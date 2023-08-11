package com.emmsale.data.eventApplyStatus

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventApplyStatusRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : EventApplyStatusRepository {

    override suspend fun getApplyStatuses(): List<EventApplyStatus> = withContext(dispatcher) {
        EventApplyStatus.values().toList()
    }

    override suspend fun getApplyStatusesByIds(ids: List<Long>): List<EventApplyStatus> =
        withContext(dispatcher) {
            EventApplyStatus.values().filter { it.id in ids }
        }
}
