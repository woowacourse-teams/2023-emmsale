package com.emmsale.data.eventdetail

import com.emmsale.data.common.ApiResult

interface EventDetailRepository {
    suspend fun fetchEventDetail(eventId: Long): ApiResult<EventDetail>
}
