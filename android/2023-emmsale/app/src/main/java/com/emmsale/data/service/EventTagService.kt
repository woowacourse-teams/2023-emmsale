package com.emmsale.data.service

import com.emmsale.data.apiModel.request.InterestEventTagUpdateRequest
import com.emmsale.data.apiModel.response.EventTagApiModel
import com.emmsale.data.apiModel.response.InterestEventTagUpdateResponseApiModel
import com.emmsale.data.common.callAdapter.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface EventTagService {
    @GET("/tags")
    suspend fun getEventTags(): ApiResponse<List<EventTagApiModel>>

    @GET("/interest-tags")
    suspend fun getInterestEventTags(
        @Query("member_id") memberId: Long,
    ): Response<List<EventTagApiModel>>

    @PUT("/interest-tags")
    suspend fun updateInterestEventTags(
        @Body requestModel: InterestEventTagUpdateRequest,
    ): Response<List<InterestEventTagUpdateResponseApiModel>>
}
