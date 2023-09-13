package com.emmsale.data.repository.interfaces

import com.emmsale.data.model.ConferenceStatus

interface ConferenceStatusRepository {
    suspend fun getConferenceStatuses(): List<ConferenceStatus>
    suspend fun getConferenceStatusByIds(ids: Array<Long>): List<ConferenceStatus>
}
