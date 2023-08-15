package com.emmsale.data.competitionStatus

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CompetitionStatusRepositoryImpl(
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
