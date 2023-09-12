package com.emmsale.data.eventTag.remote

import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.eventTag.remote.dto.EventTagApiModel
import com.emmsale.data.eventTag.remote.dto.InterestEventTagUpdateRequestApiModel
import com.emmsale.data.eventTag.remote.dto.InterestEventTagUpdateResponseApiModel
import retrofit2.Response

class EventTagRemoteDataSource(
    private val eventTagService: EventTagService,
) {
    suspend fun getEventTags(): ApiResponse<List<EventTagApiModel>> = eventTagService.getEventTags()

    suspend fun getInterestEventTags(
        memberId: Long,
    ): Response<List<EventTagApiModel>> = eventTagService.getInterestEventTags(memberId)

    suspend fun updateInterestEventTags(
        requestModel: InterestEventTagUpdateRequestApiModel,
    ): Response<List<InterestEventTagUpdateResponseApiModel>> =
        eventTagService.updateInterestEventTags(requestModel)
}
