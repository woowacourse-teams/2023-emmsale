package com.emmsale.data.conference

import com.emmsale.data.common.ApiResult

interface ConferenceRepository {
    suspend fun getConferences(
        category: EventCategory,
        year: Int? = null,
        month: Int? = null,
        statuses: List<ConferenceStatus> = emptyList(),
        tags: List<String> = emptyList(),
    ): ApiResult<List<Conference>>
}
