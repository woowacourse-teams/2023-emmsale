package com.emmsale.data.repository.concretes

import com.emmsale.data.model.CompetitionStatus
import com.emmsale.data.repository.interfaces.CompetitionStatusRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultCompetitionStatusRepository(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CompetitionStatusRepository {
    override suspend fun getCompetitionStatuses(): List<CompetitionStatus> =
        withContext(dispatcher) {
            listOf(
                CompetitionStatus.IN_PROGRESS,
                CompetitionStatus.SCHEDULED,
                CompetitionStatus.ENDED,
            )
        }

    override suspend fun getCompetitionStatusByIds(ids: Array<Long>): List<CompetitionStatus> =
        withContext(dispatcher) {
            getCompetitionStatuses().filter { it.id in ids }
        }
}
