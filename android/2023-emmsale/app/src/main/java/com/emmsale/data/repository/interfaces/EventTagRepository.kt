package com.emmsale.data.repository.interfaces

import com.emmsale.data.common.retrofit.callAdapter.ApiResponse
import com.emmsale.data.model.EventTag

interface EventTagRepository {

    suspend fun getEventTags(): ApiResponse<List<EventTag>>

    suspend fun getEventTagByIds(tagIds: Array<Long>): ApiResponse<List<EventTag>>

    suspend fun getInterestEventTags(memberId: Long): ApiResponse<List<EventTag>>

    suspend fun updateInterestEventTags(interestEventTags: List<EventTag>): ApiResponse<Unit>
}
