package com.emmsale.data.event

import com.emmsale.data.common.ApiResult

interface EventRepository {
    suspend fun getConferences(
        category: EventCategory,
        year: Int? = null,
        month: Int? = null,
        status: String? = null,
        tag: String? = null,
    ): ApiResult<List<Event>>
}
