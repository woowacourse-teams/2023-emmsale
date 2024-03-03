package com.emmsale.data.repository.concretes

import com.emmsale.data.network.di.IoDispatcher
import com.emmsale.data.repository.interfaces.ConferenceStatusRepository
import com.emmsale.model.ConferenceStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultConferenceStatusRepository @Inject constructor(
    @IoDispatcher val dispatcher: CoroutineDispatcher,
) : ConferenceStatusRepository {
    override suspend fun getConferenceStatuses(): List<ConferenceStatus> = withContext(dispatcher) {
        listOf(
            ConferenceStatus.IN_PROGRESS,
            ConferenceStatus.UPCOMING,
            ConferenceStatus.ENDED,
        )
    }

    override suspend fun getConferenceStatusByIds(tagIds: Array<Long>): List<ConferenceStatus> =
        withContext(dispatcher) {
            getConferenceStatuses().filter { it.id in tagIds }
        }
}
