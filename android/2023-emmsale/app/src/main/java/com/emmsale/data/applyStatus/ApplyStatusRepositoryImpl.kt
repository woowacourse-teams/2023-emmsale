package com.emmsale.data.applyStatus

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApplyStatusRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ApplyStatusRepository {

    override suspend fun getApplyStatuses(): List<ApplyStatus> = withContext(dispatcher) {
        ApplyStatus.values().toList()
    }

    override suspend fun getApplyStatusesByIds(ids: List<Long>): List<ApplyStatus> =
        withContext(dispatcher) {
            ApplyStatus.values().filter { it.id in ids }
        }
}
