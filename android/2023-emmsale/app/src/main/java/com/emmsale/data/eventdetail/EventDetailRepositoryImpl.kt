package com.emmsale.data.eventdetail

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.eventdetail.dto.EventDetailApiModel
import com.emmsale.data.eventdetail.mapper.toData

class EventDetailRepositoryImpl(
    private val eventDetailService: EventDetailService,
) : EventDetailRepository {
    override suspend fun getEventDetail(eventId: Long): ApiResponse<EventDetail> {
        return eventDetailService
            .getEventDetail(eventId)
            .map(EventDetailApiModel::toData)
    }
}
