package com.emmsale.data.repository.interfaces

import com.emmsale.model.ConferenceStatus

interface ConferenceStatusRepository {

    suspend fun getConferenceStatuses(): List<ConferenceStatus>

    suspend fun getConferenceStatusByIds(tagIds: Array<Long>): List<ConferenceStatus>
}
