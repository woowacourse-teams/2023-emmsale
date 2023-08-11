package com.emmsale.data.eventApplyStatus

interface EventApplyStatusRepository {

    suspend fun getApplyStatuses(): List<EventApplyStatus>

    suspend fun getApplyStatusesByIds(ids: List<Long>): List<EventApplyStatus>
}
