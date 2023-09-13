package com.emmsale.data.dataSource.remote

import com.emmsale.data.apiModel.request.InterestEventTagUpdateRequest
import com.emmsale.data.apiModel.response.EventTagApiModel
import com.emmsale.data.apiModel.response.InterestEventTagUpdateResponseApiModel
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.service.EventTagService
import retrofit2.Response

class EventTagRemoteDataSource(
    private val eventTagService: EventTagService,
) {
    suspend fun getEventTags(): ApiResponse<List<EventTagApiModel>> = eventTagService.getEventTags()

    suspend fun getInterestEventTags(
        memberId: Long,
    ): Response<List<EventTagApiModel>> = eventTagService.getInterestEventTags(memberId)

    suspend fun updateInterestEventTags(
        requestModel: InterestEventTagUpdateRequest,
    ): Response<List<InterestEventTagUpdateResponseApiModel>> =
        eventTagService.updateInterestEventTags(requestModel)
}
