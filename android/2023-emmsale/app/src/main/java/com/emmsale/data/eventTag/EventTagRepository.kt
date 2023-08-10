package com.emmsale.data.eventTag

import com.emmsale.data.common.ApiResult
import com.emmsale.data.conference.EventCategory

interface EventTagRepository {
    suspend fun getEventTags(category: EventCategory): ApiResult<List<EventTag>>
    suspend fun getEventTagByIds(ids: Array<Long>): ApiResult<List<EventTag>>
}
