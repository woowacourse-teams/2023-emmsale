package com.emmsale.data.eventTag

import com.emmsale.data.common.ApiResult
import com.emmsale.data.common.callAdapter.ApiResponse

interface EventTagRepository {
    suspend fun getEventTags(): ApiResponse<List<EventTag>>
    suspend fun getEventTagByIds(ids: Array<Long>): ApiResponse<List<EventTag>>
    suspend fun getInterestEventTags(memberId: Long): ApiResult<List<EventTag>>
    suspend fun updateInterestEventTags(interestEventTags: List<EventTag>): ApiResult<Unit>
}
