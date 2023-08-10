package com.emmsale.data.conferenceStatus

interface ConferenceStatusRepository {
    suspend fun getConferenceStatuses(): List<ConferenceStatus>
    suspend fun getConferenceStatusByIds(ids: Array<Long>): List<ConferenceStatus>
}