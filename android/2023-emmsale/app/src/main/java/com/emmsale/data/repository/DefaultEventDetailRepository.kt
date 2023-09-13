package com.emmsale.data.repository

import com.emmsale.data.apiModel.response.EventDetailApiModel
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.mapper.toData
import com.emmsale.data.model.EventDetail
import com.emmsale.data.service.EventDetailService

class DefaultEventDetailRepository(
    private val eventDetailService: EventDetailService,
) : EventDetailRepository {
    override suspend fun getEventDetail(eventId: Long): ApiResponse<EventDetail> {
        return eventDetailService
            .getEventDetail(eventId)
            .map(EventDetailApiModel::toData)
    }
}
