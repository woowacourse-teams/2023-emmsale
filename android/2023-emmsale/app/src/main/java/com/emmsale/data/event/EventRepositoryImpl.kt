package com.emmsale.data.event

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.event.dto.EventApiModel
import com.emmsale.data.event.dto.toData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val eventService: EventService,
) : EventRepository {
    override suspend fun getEvents(
        year: Int, month: Int,
        status: String, tags: List<String>
    ): ApiResult<List<Event>> = withContext(dispatcher) {
        handleApi(eventService.getEvents(year, month, status, tags), List<EventApiModel>::toData)
    }
}
