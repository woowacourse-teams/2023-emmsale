package com.emmsale.data.eventdetail

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.eventdetail.dto.EventDetailApiModel

class EventDetailRepositoryImpl(
    private val eventDetailService: EventDetailService,
) : EventDetailRepository {

    override suspend fun fetchEventDetail(eventId: Long): ApiResult<EventDetail> {
        return handleApi(
            execute = { eventDetailService.fetchEventDetail(eventId) },
            mapToDomain = EventDetailApiModel::toData,
        )
    }
}
