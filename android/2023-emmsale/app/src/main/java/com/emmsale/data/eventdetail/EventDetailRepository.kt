package com.emmsale.data.eventdetail

import com.emmsale.data.common.ApiResult

interface EventDetailRepository {
    suspend fun getEventDetail(eventId: Long): ApiResult<EventDetail>

    suspend fun scrapEvent(eventId: Long): ApiResult<Unit>
}
