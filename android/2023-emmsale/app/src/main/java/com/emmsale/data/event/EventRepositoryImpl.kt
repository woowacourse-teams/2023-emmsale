package com.emmsale.data.event

import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.common.handleApi
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
        when (val events = handleApi { eventService.getEvents(year, month, status, tags) }) {
            is ApiSuccess -> ApiSuccess(events.data.map(Event::from))
            is ApiError -> ApiError(events.code, events.message)
            is ApiException -> ApiException(events.e)
        }
    }
}
