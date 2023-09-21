package com.emmsale.data.dataSource.remote

import com.emmsale.data.apiModel.request.InterestEventTagUpdateRequest
import com.emmsale.data.apiModel.response.EventTagResponse
import com.emmsale.data.apiModel.response.UpdatedMemberInterestEventTagResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import com.emmsale.data.service.EventTagService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventTagRemoteDataSource @Inject constructor(
    private val eventTagService: EventTagService,
) {
    suspend fun getEventTags(): ApiResponse<List<EventTagResponse>> = eventTagService.getEventTags()

    suspend fun getInterestEventTags(
        memberId: Long,
    ): ApiResponse<List<EventTagResponse>> {
        return eventTagService.getInterestEventTags(memberId)
    }

    suspend fun updateInterestEventTags(
        requestModel: InterestEventTagUpdateRequest,
    ): ApiResponse<List<UpdatedMemberInterestEventTagResponse>> {
        return eventTagService.updateInterestEventTags(requestModel)
    }
}
