package com.emmsale.data.service

import com.emmsale.data.apiModel.request.InterestEventTagUpdateRequest
import com.emmsale.data.apiModel.response.EventTagResponse
import com.emmsale.data.apiModel.response.UpdatedMemberInterestEventTagResponse
import com.emmsale.data.common.callAdapter.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface EventTagService {
    @GET("/tags")
    suspend fun getEventTags(): ApiResponse<List<EventTagResponse>>

    @GET("/interest-tags")
    suspend fun getInterestEventTags(
        @Query("member_id") memberId: Long,
    ): Response<List<EventTagResponse>>

    @PUT("/interest-tags")
    suspend fun updateInterestEventTags(
        @Body requestModel: InterestEventTagUpdateRequest,
    ): Response<List<UpdatedMemberInterestEventTagResponse>>
}
