package com.emmsale.data.conferenceStatus

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConferenceStatusRepositoryImpl(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ConferenceStatusRepository {
    override suspend fun getConferenceStatuses(): List<ConferenceStatus> = withContext(dispatcher) {
        listOf(
            ConferenceStatus.IN_PROGRESS,
            ConferenceStatus.SCHEDULED,
            ConferenceStatus.ENDED,
        )
    }

    override suspend fun getConferenceStatusByIds(ids: Array<Long>): List<ConferenceStatus> =
        withContext(dispatcher) {
            getConferenceStatuses().filter { it.id in ids }
        }
}
