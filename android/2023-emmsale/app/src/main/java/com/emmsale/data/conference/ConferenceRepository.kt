package com.emmsale.data.conference

import com.emmsale.data.common.ApiResult

interface ConferenceRepository {
    suspend fun getConferences(
        category: EventCategory,
        year: Int? = null,
        month: Int? = null,
        status: ConferenceStatus? = null,
        tag: String? = null,
    ): ApiResult<List<Conference>>
}
