package com.emmsale.data.network.service

import com.emmsale.data.network.apiModel.response.ActivityResponse
import com.emmsale.data.network.callAdapter.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ActivityService {
    @GET("/activities")
    suspend fun getActivities(): ApiResponse<List<ActivityResponse>>

    @GET("/members/{memberId}/activities")
    suspend fun getActivities(
        @Path("memberId") memberId: Long,
    ): ApiResponse<List<ActivityResponse>>
}
