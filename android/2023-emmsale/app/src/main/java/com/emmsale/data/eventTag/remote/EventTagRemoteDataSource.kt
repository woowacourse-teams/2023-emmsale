package com.emmsale.data.eventTag.remote

import com.emmsale.data.eventTag.remote.dto.EventTagApiModel
import retrofit2.Response

class EventTagRemoteDataSource(
    private val eventTagService: EventTagService,
) {
    suspend fun getInterestEventTags(
        memberId: Long,
    ): Response<List<EventTagApiModel>> = eventTagService.getInterestEventTags(memberId)
}
