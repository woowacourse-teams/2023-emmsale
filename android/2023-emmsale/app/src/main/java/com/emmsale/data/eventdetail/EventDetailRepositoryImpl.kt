package com.emmsale.data.eventdetail

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.eventdetail.dto.EventDetailApiModel
import com.emmsale.data.eventdetail.mapper.toData

class EventDetailRepositoryImpl(
    private val eventDetailService: EventDetailService,
) : EventDetailRepository {

    override suspend fun getEventDetail(eventId: Long): ApiResult<EventDetail> {
        return handleApi(
            execute = { eventDetailService.getEventDetail(eventId) },
            mapToDomain = EventDetailApiModel::toData,
        )
    }
}
