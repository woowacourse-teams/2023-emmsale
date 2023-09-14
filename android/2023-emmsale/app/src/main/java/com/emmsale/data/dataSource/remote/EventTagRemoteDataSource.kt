package com.emmsale.data.dataSource.remote

import com.emmsale.data.apiModel.request.InterestEventTagUpdateRequest
import com.emmsale.data.apiModel.response.EventTagResponse
import com.emmsale.data.apiModel.response.UpdatedMemberInterestEventTagResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.service.EventTagService
import retrofit2.Response

class EventTagRemoteDataSource(
    private val eventTagService: EventTagService,
) {
    suspend fun getEventTags(): ApiResponse<List<EventTagResponse>> = eventTagService.getEventTags()

    suspend fun getInterestEventTags(
        memberId: Long,
    ): Response<List<EventTagResponse>> = eventTagService.getInterestEventTags(memberId)

    suspend fun updateInterestEventTags(
        requestModel: InterestEventTagUpdateRequest,
    ): Response<List<UpdatedMemberInterestEventTagResponse>> =
        eventTagService.updateInterestEventTags(requestModel)
}
