package com.emmsale.data.event

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.event.dto.ConferenceApiModel
import com.emmsale.data.event.dto.toData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val eventService: EventService,
) : EventRepository {
    override suspend fun getConferences(
        year: Int?,
        month: Int?,
        status: String?,
        tag: String?,
    ): ApiResult<List<Event>> = withContext(dispatcher) {
        handleApi(
            eventService.getEvents(year, month, status, tag),
            List<ConferenceApiModel>::toData
        )
    }
}
