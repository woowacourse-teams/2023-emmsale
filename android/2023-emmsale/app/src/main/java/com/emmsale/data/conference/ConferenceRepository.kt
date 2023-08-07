package com.emmsale.data.conference

import com.emmsale.data.common.ApiResult

interface ConferenceRepository {
    suspend fun getConferences(
        category: EventCategory,
        startDate: String? = null,
        endDate: String? = null,
        statuses: List<ConferenceStatus> = emptyList(),
        tags: List<String> = emptyList(),
    ): ApiResult<List<Conference>>
}
