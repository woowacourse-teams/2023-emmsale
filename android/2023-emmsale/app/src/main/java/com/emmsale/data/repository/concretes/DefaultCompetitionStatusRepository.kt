package com.emmsale.data.repository.concretes

import com.emmsale.model.CompetitionStatus
import com.emmsale.data.repository.interfaces.CompetitionStatusRepository
import com.emmsale.data.network.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultCompetitionStatusRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : CompetitionStatusRepository {
    override suspend fun getCompetitionStatuses(): List<CompetitionStatus> =
        withContext(dispatcher) {
            listOf(
                CompetitionStatus.IN_PROGRESS,
                CompetitionStatus.SCHEDULED,
                CompetitionStatus.ENDED,
            )
        }

    override suspend fun getCompetitionStatusByIds(tagIds: Array<Long>): List<CompetitionStatus> =
        withContext(dispatcher) {
            getCompetitionStatuses().filter { it.id in tagIds }
        }
}
