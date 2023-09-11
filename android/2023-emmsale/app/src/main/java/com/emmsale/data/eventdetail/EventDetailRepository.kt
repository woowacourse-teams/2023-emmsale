package com.emmsale.data.eventdetail

import com.emmsale.data.common.callAdapter.ApiResponse

interface EventDetailRepository {
    suspend fun getEventDetail(eventId: Long): ApiResponse<EventDetail>
}
