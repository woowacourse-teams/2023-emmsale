package com.emmsale.data.event

import com.emmsale.data.common.ApiResult

interface EventRepository {
    suspend fun getEvents(
        year: Int, month: Int,
        status: String, tags: List<String>
    ): ApiResult<List<Event>>
}
