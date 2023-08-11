package com.emmsale.data.eventApplyStatus

interface EventApplyStatusRepository {

    suspend fun getEventApplyStatuses(): List<EventApplyStatus>

    suspend fun getEventApplyStatusesByIds(ids: List<Long>): List<EventApplyStatus>
}
