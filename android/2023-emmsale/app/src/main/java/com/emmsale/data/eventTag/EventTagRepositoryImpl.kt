package com.emmsale.data.eventTag

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.handleApi
import com.emmsale.data.event.EventCategory
import com.emmsale.data.eventTag.dto.EventTagApiModel
import com.emmsale.data.eventTag.mapper.toData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventTagRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val eventTagService: EventTagService,
) : EventTagRepository {
    override suspend fun getEventTags(category: EventCategory): ApiResult<List<EventTag>> =
        withContext(dispatcher) {
            handleApi(
                execute = { eventTagService.getEventTags(category.text) },
                mapToDomain = List<EventTagApiModel>::toData,
            )
        }

    override suspend fun getEventTagByIds(ids: Array<Long>): ApiResult<List<EventTag>> {
        return withContext(dispatcher) {
            handleApi(
                execute = { eventTagService.getEventTagByIds(ids) },
                mapToDomain = List<EventTagApiModel>::toData,
            )
        }
    }
}
