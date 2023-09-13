package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.model.EventDetail

interface EventDetailRepository {
    suspend fun getEventDetail(eventId: Long): ApiResponse<EventDetail>
}
